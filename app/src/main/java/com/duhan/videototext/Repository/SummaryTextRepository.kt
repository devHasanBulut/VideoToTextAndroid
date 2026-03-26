    package com.duhan.videototext.Repository

    import android.content.Context
import android.provider.Settings
import com.duhan.videototext.Data.LocalDataSource.SummaryForCategories
import com.duhan.videototext.Data.LocalDataSource.SummaryTextModel
import com.duhan.videototext.Data.LocalDataSource.SummaryTextModelDao
import com.duhan.videototext.Data.RemoteDataSource.VideoToTextApiService
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


    class SummaryTextRepository @Inject constructor(
        private val summaryTextModelDao: SummaryTextModelDao,
        private val api: VideoToTextApiService,
        @ApplicationContext private val context: Context
    ) {
        
        private val androidId: String by lazy {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        }

        suspend fun fetchAllSummaryTexts(): List<SummaryTextModel> {
            return api.getAllSummaryText()
        }

        private fun normalizeUrl(url: String): String {
            return url.trim().let { rawUrl ->
                // URL'deki tekrarları ve hataları düzelt
                when {
                    // "youtuhttps://www.youtube.com" gibi hatalı formatları düzelt
                    rawUrl.contains("youtuhttps://") -> {
                        rawUrl.replace(Regex("youtu(https?://)"), "https://")
                            .replace(Regex("(https://www\\.youtube\\.com.*?)(https?://www\\.youtube\\.com)"), "$1")
                    }
                    // "youtuhttp://www.youtube.com" gibi hatalı formatları düzelt
                    rawUrl.contains("youtuhttp://") -> {
                        rawUrl.replace(Regex("youtu(http://)"), "https://")
                            .replace(Regex("(https://www\\.youtube\\.com.*?)(https?://www\\.youtube\\.com)"), "$1")
                    }
                    // URL içinde tekrar eden youtube.com varsa düzelt
                    rawUrl.matches(Regex(".*https?://www\\.youtube\\.com.*https?://www\\.youtube\\.com.*")) -> {
                        // İlk geçerli URL'yi al
                        val match = Regex("(https?://www\\.youtube\\.com[^\\s]+)").find(rawUrl)
                        match?.value ?: rawUrl
                    }
                    else -> rawUrl
                }
            }
        }
        
        suspend fun fetchSummaryByUrl(url: String, ratio: Int, style: String): SummaryTextModel {
            val normalizedUrl = normalizeUrl(url)
            return try {
                api.fetchSummaryByUrl(normalizedUrl, ratio,style, androidId)
            } catch (e: HttpException) {
                // HTTP hatalarını parse et ve daha anlaşılır mesaj oluştur
                val errorMessage = parseHttpError(e)
                throw IOException(errorMessage, e)
            } catch (e: IOException) {
                // Ağ hataları için
                throw IOException("İnternet bağlantınızı kontrol edin veya daha sonra tekrar deneyin.", e)
            } catch (e: Exception) {
                // Diğer hatalar
                throw IOException("Bir hata oluştu: ${e.message ?: "Bilinmeyen hata"}", e)
            }
        }
        
        private fun parseHttpError(e: HttpException): String {
            return try {
                val errorBody = e.response()?.errorBody()?.string()
                if (!errorBody.isNullOrBlank()) {
                    try {
                        val gson = Gson()
                        val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                        val message = jsonObject.get("message")?.asString
                        if (!message.isNullOrBlank()) {
                            // Sunucu mesajını temizle ve kullanıcı dostu hale getir
                            return formatErrorMessage(message, e.code())
                        }
                    } catch (ex: Exception) {
                        // JSON parse hatası, genel mesaj döndür
                    }
                }
                // Hata kodu bazlı mesaj
                formatErrorMessage(null, e.code())
            } catch (ex: Exception) {
                formatErrorMessage(null, e.code())
            }
        }
        
        private fun formatErrorMessage(serverMessage: String?, statusCode: Int): String {
            return when (statusCode) {
                400 -> "Geçersiz istek. Lütfen URL'yi kontrol edin."
                401 -> "Yetkilendirme hatası. Lütfen tekrar deneyin."
                403 -> "Erişim reddedildi."
                404 -> "İstenen kaynak bulunamadı."
                500 -> {
                    if (serverMessage != null) {
                        when {
                            // Gemini model hatası
                            serverMessage.contains("gemini", ignoreCase = true) || 
                            serverMessage.contains("models/", ignoreCase = true) ||
                            serverMessage.contains("not found for API version", ignoreCase = true) -> {
                                "AI servisi şu anda kullanılamıyor. Lütfen daha sonra tekrar deneyin."
                            }
                            // Genel sunucu hatası
                            else -> {
                                // Sunucu mesajından gereksiz detayları temizle
                                val cleanMessage = serverMessage
                                    .replace(Regex("<EOL>"), " ")
                                    .replace(Regex("\\s+"), " ")
                                    .trim()
                                
                                // Eğer mesaj çok uzunsa kısalt
                                if (cleanMessage.length > 100) {
                                    "Sunucu hatası oluştu. Lütfen daha sonra tekrar deneyin."
                                } else {
                                    "Sunucu hatası: $cleanMessage"
                                }
                            }
                        }
                    } else {
                        "Sunucu hatası oluştu. Lütfen daha sonra tekrar deneyin."
                    }
                }
                502, 503, 504 -> "Sunucu şu anda kullanılamıyor. Lütfen daha sonra tekrar deneyin."
                else -> {
                    // Sunucu mesajını temizle ve göster
                    val cleanMessage = serverMessage
                        ?.replace(Regex("<EOL>"), " ")
                        ?.replace(Regex("\\s+"), " ")
                        ?.trim()
                        ?.takeIf { it.length <= 150 }
                    cleanMessage ?: "Bir hata oluştu. Lütfen tekrar deneyin."
                }
            }
        }

        suspend fun getOrFetchSummary(url: String, ratio: Int, style: String): SummaryTextModel {
            return withContext(Dispatchers.IO) {
                val normalizedUrl = normalizeUrl(url)
                
                val localSummary = summaryTextModelDao.getSummaryByUrl(normalizedUrl)

                if (localSummary != null) {
                    localSummary
                } else {
                    val remoteSummary = fetchSummaryByUrl(normalizedUrl, ratio, style)
                    summaryTextModelDao.insertSummaryText(remoteSummary)
                    remoteSummary
                }
            }
        }

         suspend fun refreshLocalDatabase() {
            val remoteData = fetchAllSummaryTexts()
            summaryTextModelDao.clearAll()
            remoteData.forEach { summaryTextModelDao.insertSummaryText(it) }
        }



        suspend fun getAllSummaryText(): List<SummaryTextModel> {
            return summaryTextModelDao.getAllSummaryText()
        }


        suspend fun getSummaryTextById(id: Int): SummaryTextModel? {
            return summaryTextModelDao.getSummaryTextById(id)
        }

        suspend fun assignCategoryToSummary(summaryId: Int, categoryId: Int) {
            val crossRef = SummaryForCategories(summaryId = summaryId, categoryId = categoryId)
            summaryTextModelDao.insertSummaryCategoryCrossRef(crossRef)
        }

        suspend fun getSummariesByCategoryId(categoryId: Int): List<SummaryTextModel> {
            return summaryTextModelDao.getSummariesByCategoryId(categoryId)
        }

        suspend fun deleteSummaryText(summaryId: Int) {
            summaryTextModelDao.deleteSummaryTextById(summaryId)
        }

        suspend fun getSummaryCountByCategoryId(categoryId: Int): Int {
            return summaryTextModelDao.getSummaryCountByCategoryId(categoryId)
        }

        suspend fun removeCategoryFromSummary(summaryId: Int, categoryId: Int) {
            summaryTextModelDao.removeCategoryFromSummary(summaryId, categoryId)
        }

        suspend fun isSummaryInCategory(summaryId: Int, categoryId: Int): Boolean {
            return summaryTextModelDao.isSummaryInCategory(summaryId, categoryId)
        }

        suspend fun getUserStatus(): com.duhan.videototext.Data.RemoteDataSource.UserStatusResponse {
            return try {
                api.getUserStatus(androidId)
            } catch (e: Exception) {
                // Hata olursa (internet yoksa vb.) kullanıcıyı Free modda başlat
                com.duhan.videototext.Data.RemoteDataSource.UserStatusResponse(
                    isPremium = false,
                    remainingQuota = 0,
                    summaryCount = 0
                )
            }
        }

    }