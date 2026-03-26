package com.duhan.videototext.Data.LocalDataSource

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoriesModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryName: String,
)
