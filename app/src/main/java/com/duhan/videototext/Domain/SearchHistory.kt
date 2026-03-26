package com.duhan.videototext.Domain

import com.duhan.videototext.Repository.SearchHistoryRepository
import javax.inject.Inject

class SearchHistory @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    suspend fun resetIdAutoIncrement() {
        searchHistoryRepository.resetIdAutoIncrement()
    }
    suspend fun deleteSearchHistoryByName(searchHistoryName: String) {
        searchHistoryRepository.deleteSearchHistoryByName(searchHistoryName)
    }
    suspend fun deleteAllSearchHistory() {
        searchHistoryRepository.deleteAllSearchHistory()
    }

    suspend fun getAllSearchHistory() = searchHistoryRepository.getAllSearchHistories()

    suspend fun executeSearchHistory(searchHistory: String) {
        searchHistoryRepository.createSearchHistory(searchHistory)
    }
}



