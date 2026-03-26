package com.duhan.videototext.Data.RemoteDataSource

import com.duhan.videototext.Data.LocalDataSource.SummaryTextModel
import retrofit2.http.GET
import retrofit2.http.Query


interface VideoToTextApiService {

    @GET("/api/summary")
    suspend fun getAllSummaryText(): List<SummaryTextModel>


    @GET("/api/summary/fetch")
    suspend fun fetchSummaryByUrl(
        @Query("url") url: String, 
        @Query("ratio") ratio: Int,
        @Query("style") style: String,
        @Query("androidId") androidId: String
    ): SummaryTextModel

    @GET("/api/summary/user-status")
    suspend fun getUserStatus(
        @Query("androidId") androidId: String
    ): UserStatusResponse
}
