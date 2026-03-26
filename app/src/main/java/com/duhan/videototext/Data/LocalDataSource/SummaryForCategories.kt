package com.duhan.videototext.Data.LocalDataSource

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "summaryForCategories",
    primaryKeys = ["summaryId", "categoryId"],
    indices = [Index(value = ["categoryId"])],
)
data class SummaryForCategories(
    val summaryId: Int,
    val categoryId: Int
)
