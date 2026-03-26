package com.duhan.videototext.Data.LocalDataSource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchHistoryModelDao {

    @Query("SELECT * FROM searchHistory")
    suspend fun getAllSearchHistory(): List<SearchHistoryModel>

    @Query("SELECT * FROM searchHistory WHERE searchQuery = :searchHistoryName")
    suspend  fun getSearchHistoryByName(searchHistoryName: String): SearchHistoryModel?

    @Insert
    suspend  fun createSearchQuery(searchHistory: SearchHistoryModel)

    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistoryModel)

    @Query("DELETE FROM searchHistory WHERE searchQuery = :searchQuery")
    suspend fun deleteSearchHistoryByName(searchQuery: String)

    @Query("DELETE FROM sqlite_sequence WHERE name = 'searchHistory'")
    suspend fun resetAutoIncrement()

    @Query("DELETE FROM searchHistory")
    suspend fun deleteAllSearchHistory()
}

