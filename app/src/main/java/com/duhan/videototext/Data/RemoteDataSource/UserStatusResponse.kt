package com.duhan.videototext.Data.RemoteDataSource

import com.google.gson.annotations.SerializedName

data class UserStatusResponse(
    @SerializedName("isPremium")
    val isPremium: Boolean,

    @SerializedName("remainingRights")
    val remainingQuota: Int,

    @SerializedName("usageCount")
    val summaryCount: Int
)
