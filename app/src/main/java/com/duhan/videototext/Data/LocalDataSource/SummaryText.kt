package com.duhan.videototext.Data.LocalDataSource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "summaryText", indices = [Index(value = ["url"], unique = true)],
)
data class SummaryTextModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("originalUrl")
    @ColumnInfo(name = "url")
    val url: String,
    @SerializedName("summaryText")
    val summaryText: String,
    @SerializedName("videoId")
    val videoId: String?,
    @ColumnInfo(name = "video_title")
    val videoTitle: String? = null,

)
