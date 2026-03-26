package com.duhan.videototext.Presentation

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duhan.videototext.Data.LocalDataSource.SearchHistoryModel
import com.duhan.videototext.Data.LocalDataSource.SummarySettingsManager
import com.duhan.videototext.Data.LocalDataSource.SummaryTextModel
import com.duhan.videototext.Domain.Categories
import com.duhan.videototext.Domain.Category
import com.duhan.videototext.Domain.GetUserStatus
import com.duhan.videototext.Domain.SearchHistory
import com.duhan.videototext.Domain.SummaryText
import com.duhan.videototext.Presentation.SelectedCategoryScreen.VideoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val summary: SummaryTextModel) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    object QuotaExceeded : SearchUiState()
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val app: Application,
    private val insertCategory: Categories,
    private val getCategories: Categories,
    private val insertSearchHistory: SearchHistory,
    private val getAllSearchHistory: SearchHistory,
    private val deleteSearchHistory: SearchHistory,
    private  val deleteCategory: Categories,
    private val getSummaryText: SummaryText,
    private val getOrFetchSummaryUseCase: SummaryText,
    private val summaryText: SummaryText,
    private val summarySettingsManager: SummarySettingsManager,
    private val getUserStatus: GetUserStatus

): AndroidViewModel(app)
{
    companion object {
        const val FavoritesCategoryId = 1
        const val DownloadsCategoryId = 2
    }

    private val _isPremiumUser = MutableStateFlow(false)
    val isPremiumUser = _isPremiumUser.asStateFlow()

    private val _remainingQuota = MutableStateFlow(0)
    val remainingQuota = _remainingQuota.asStateFlow()
    
    private val _adsWatchedToday = MutableStateFlow(0)
    val adsWatchedToday = _adsWatchedToday.asStateFlow()
    
    private val _adsNeededForNextQuota = MutableStateFlow(0)
    val adsNeededForNextQuota = _adsNeededForNextQuota.asStateFlow()



    private val _selectedSummary = MutableStateFlow<SummaryTextModel?>(null)
    val selectedSummary = _selectedSummary.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()


    private val _videosForCategory = MutableStateFlow<List<SummaryTextModel>>(emptyList())

    private val _searchHistory = mutableStateListOf<SearchHistoryModel>()
    val searchHistoryList: List<SearchHistoryModel> get() = _searchHistory

    private val _summaryTextList = mutableStateListOf<String>()
    val summaryTextList: List<String> get() = _summaryTextList


    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState = _searchUiState.asStateFlow()

    private val _lastScannedSummaries = MutableStateFlow<List<SummaryTextModel>>(emptyList())
    val lastScannedSummaries = _lastScannedSummaries.asStateFlow()

    private val _categoryCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val categoryCounts = _categoryCounts.asStateFlow()
    
    init {
        //getAllCategory()
        getAllSearchHistory()
        loadSummaryTextsFromServer()
        loadLastScannedSummaries()
        initializeQuota()
        checkUserStatus()
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getCategories.initDefaultCategories()
                Log.d("CategoryDebug", "default kategoriler kontrol edildi.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CategoryDebug", "HATA: default kategori eklenirken sorun: ${e.message}")
            }

            val updatedList = getCategories.getAllCategories()
            _categories.value = updatedList

            Log.d("CategoryDebug", "UI güncellendi. listenin noyutu: ${updatedList.size}")
        }
    }
    
    private fun initializeQuota() {
        // premium değilse 1 hakkı ver premiumsa sınırsız
        if (!_isPremiumUser.value) {
            _remainingQuota.value = 1
        } else {
            _remainingQuota.value = Int.MAX_VALUE
        }
    }

    fun onSearchedTextChanged(newText: String) {
        _searchText.value = newText
    }

    private fun loadLastScannedSummaries() {
        viewModelScope.launch {
            val allSummaries = getSummaryText.executeGetAllSummaryTextModels()
            Log.d("SonTarananlarDebug", "Veritabanından çekilen özet sayısı: ${allSummaries.size}")

            _lastScannedSummaries.value = allSummaries.takeLast(5).reversed()
        }
    }

    fun searchForUrl(url: String) {
        if (url.isBlank()) {
            _searchUiState.value = SearchUiState.Error("URL boş olamaz.")
            return
        }
        
        if (!_isPremiumUser.value) {
            if (_remainingQuota.value <= 0) {
                _searchUiState.value = SearchUiState.QuotaExceeded
                return
            }
        }
        
        viewModelScope.launch {
            val ratio = summarySettingsManager.getSummaryRatio()
            _searchUiState.value = SearchUiState.Loading
            val result = getOrFetchSummaryUseCase(url,ratio)

            result.onSuccess { summaryModel ->
                if (!_isPremiumUser.value) {
                    _remainingQuota.value = (_remainingQuota.value - 1).coerceAtLeast(0)
                }
                
                assignSummaryToCategory(summaryModel.id, DownloadsCategoryId)
                
                loadLastScannedSummaries()
                
                _searchUiState.value = SearchUiState.Success(summaryModel)
            }.onFailure { error ->
                if (error.message?.contains("LIMIT_EXCEEDED") == true || error.message?.contains("403") == true) {
                    _searchUiState.value = SearchUiState.QuotaExceeded
                    checkUserStatus()
                } else {
                    _searchUiState.value = SearchUiState.Error(error.message ?: "Bilinmeyen bir hata oluştu.")
                }
            }
        }
    }

    val videosForCategoryUi: StateFlow<List<VideoUiModel>> = _videosForCategory
        .map { summaryModelList ->
            summaryModelList.map { summary ->
                VideoUiModel(
                    id = summary.id,
                    thumbnailUrl = summary.videoId?.let { "https://img.youtube.com/vi/$it/mqdefault.jpg" },
                    title = summary.videoTitle ?:"Video Summary",
                    summarySnippet = summary.summaryText.split(" ")
                        .take(12)
                        .joinToString(" ") + "..."
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun checkUserStatus() {
        viewModelScope.launch {
            try {
                val currentAndroidId = Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
                Log.d("UserStatus", "Cihazın Şu Anki ID'si: $currentAndroidId")

                val status = getUserStatus()

                Log.d("UserStatus", "Sunucu Yanıtı -> Premium: ${status.isPremium}, Kalan: ${status.remainingQuota}")

                _isPremiumUser.value = status.isPremium

                if (status.isPremium) {
                    _remainingQuota.value = Int.MAX_VALUE
                } else {
                    _remainingQuota.value = status.remainingQuota
                }

            } catch (e: Exception) {
                Log.e("UserStatus", "Kullanıcı durumu alınamadı, varsayılanlar kullanılıyor.", e)
                _isPremiumUser.value = false
                _remainingQuota.value = 0
            }
        }
    }

    fun addNewCategoryAndAssignToSummary(categoryName: String, summaryId: Int) {
        viewModelScope.launch(Dispatchers.IO){
            insertCategory.insertCategory(categoryName)
            val newCategory = getCategories.getCategoryByName(categoryName)
            newCategory?.let {
                summaryText.executeAssignCategoryToSummary(summaryId, it.id)
            }
            withContext(Dispatchers.Main) {
                refreshCategories()
            }
        }
    }
    fun getSummaryById(id: Int) {
        viewModelScope.launch {
            _selectedSummary.value = getSummaryText.executeGetSummaryTextById(id)
            _isFavorite.value = summaryText.executeIsSummaryInCategory(id, FavoritesCategoryId)
        }
    }

    fun toggleFavorite(summaryId: Int) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                summaryText.executeRemoveCategoryFromSummary(summaryId, FavoritesCategoryId)
                _isFavorite.value = false
            } else {
                summaryText.executeAssignCategoryToSummary(summaryId, FavoritesCategoryId)
                _isFavorite.value = true
            }
            refreshCategories()
        }
    }

    fun loadVideosForCategory(categoryId: Int) {
        viewModelScope.launch {
            _videosForCategory.value = summaryText.executeGetSummariesByCategoryId(categoryId)
        }
    }

    fun assignSummaryToCategory(summaryId: Int, categoryId: Int) {
        viewModelScope.launch {
            summaryText.executeAssignCategoryToSummary(summaryId, categoryId)
            refreshCategories()
        }
    }


    fun addNewSearchHistory(query: String) {
        viewModelScope.launch {
            val isAlreadyExists = searchHistoryList.any { it.searchQuery == query.trim() }

            if (!isAlreadyExists) {
                insertSearchHistory.executeSearchHistory(query.trim())
                getAllSearchHistory()
            }
        }
    }


    fun resetSearchState() {
        _searchUiState.value = SearchUiState.Idle
    }

    private fun getAllSearchHistory() {
        viewModelScope.launch {
            val searchHistoryFromDb = getAllSearchHistory.getAllSearchHistory()
            _searchHistory.clear()
            _searchHistory.addAll(searchHistoryFromDb)
        }
    }

    fun loadSummaryTextsFromServer() {
        viewModelScope.launch {
            val summaries = getSummaryText.executeGetAllSummaryText()
            _summaryTextList.clear()
            _summaryTextList.addAll(summaries)
        }
    }

    fun refreshCategories() {
        viewModelScope.launch {
            val categoriesFromDb = getCategories.getAllCategories()
            _categories.value = categoriesFromDb

            val counts = mutableMapOf<Int, Int>()
            categoriesFromDb.forEach { category ->
                val count = summaryText.executeGetSummaryCountByCategoryId(category.id)
                counts[category.id] = count
            }
            _categoryCounts.value = counts
        }
    }



    fun addNewCategory(categoryName: String) {
        viewModelScope.launch {
            insertCategory.insertCategory(
                categoryName
            )
            refreshCategories()
        }
    }

    fun deleteSearchHistory(searchHistoryName: String) {
        viewModelScope.launch{
            deleteSearchHistory.deleteSearchHistoryByName(searchHistoryName)
            deleteSearchHistory.resetIdAutoIncrement()
            getAllSearchHistory()
        }
    }

    fun deleteAllSearchHistory(){
        viewModelScope.launch {
            deleteSearchHistory.deleteAllSearchHistory()
            getAllSearchHistory()
        }
    }

    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            deleteCategory.deleteCategory(categoryName)
            refreshCategories()
        }
    }




    fun saveSummaryRatio(ratio: Int) {
        summarySettingsManager.saveSummaryRatio(ratio)
    }

    fun getSummaryRatio(): Int {
        return summarySettingsManager.getSummaryRatio()
    }

    fun deleteSummaryText(summaryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            summaryText.executeDeleteSummaryText(summaryId)
            loadLastScannedSummaries()
        }
    }
}


