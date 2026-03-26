package com.duhan.videototext.Domain

import com.duhan.videototext.Data.RemoteDataSource.UserStatusResponse
import com.duhan.videototext.Repository.SummaryTextRepository
import javax.inject.Inject

class GetUserStatus@Inject constructor(
    private val repository: SummaryTextRepository
) {
    suspend operator fun invoke(): UserStatusResponse {
        return repository.getUserStatus()
    }
}