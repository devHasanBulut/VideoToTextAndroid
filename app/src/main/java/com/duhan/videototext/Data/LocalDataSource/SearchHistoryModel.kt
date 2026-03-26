package com.duhan.videototext.Data.LocalDataSource

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistory")
data class SearchHistoryModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val searchQuery: String,
)
