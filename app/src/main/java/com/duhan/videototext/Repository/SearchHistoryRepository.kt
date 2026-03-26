package com.duhan.videototext.Repository

import com.duhan.videototext.Data.LocalDataSource.SearchHistoryModel
import com.duhan.videototext.Data.LocalDataSource.SearchHistoryModelDao
import javax.inject.Inject

class SearchHistoryRepository @Inject constructor(
   private val searchHistoryModelDao: SearchHistoryModelDao
) {
    suspend fun createSearchHistory(query: String): Int {
        val newItem = SearchHistoryModel(searchQuery = query)
        searchHistoryModelDao.createSearchQuery(newItem)
        return searchHistoryModelDao.getAllSearchHistory().lastOrNull()?.id ?: 0
    }

    suspend fun getAllSearchHistories(): List<SearchHistoryModel> {
        return searchHistoryModelDao.getAllSearchHistory()
    }
    suspend fun deleteAllSearchHistory() {
        searchHistoryModelDao.deleteAllSearchHistory()
    }
    suspend fun deleteSearchHistoryByName(searchHistoryName: String) {
        searchHistoryModelDao.deleteSearchHistoryByName(searchHistoryName)
    }

    suspend fun resetIdAutoIncrement() {
        searchHistoryModelDao.resetAutoIncrement()
    }

}