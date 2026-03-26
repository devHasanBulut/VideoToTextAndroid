package com.duhan.videototext.Domain

import com.duhan.videototext.Data.LocalDataSource.SummaryTextModel
import com.duhan.videototext.Repository.SummaryTextRepository
import java.io.IOException
import javax.inject.Inject

class SummaryText @Inject constructor(
    private  val summaryTextRepository: SummaryTextRepository
) {

    suspend operator fun invoke(
        url: String,
        ratio: Int,
        style: String = SummaryStyle.STANDARD.name
    ): Result<SummaryTextModel> {
        return try {
            val summary = summaryTextRepository.getOrFetchSummary(url, ratio, style)
            Result.success(summary)
        } catch (e: IOException) {
            // Ağ ve HTTP hataları
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(IOException(" hata oluştu: ${e.message ?: ""}", e))
        }
    }
    suspend fun executeGetAllSummaryTextModels(): List<SummaryTextModel> {
        return summaryTextRepository.getAllSummaryText()
    }

    suspend fun executeGetAllSummaryText(): List<String> {
        return summaryTextRepository.getAllSummaryText().map { it.summaryText }
    }

    suspend fun executeGetSummaryTextById(id: Int): SummaryTextModel? {
        return summaryTextRepository.getSummaryTextById(id)
    }

    suspend fun executeAssignCategoryToSummary(summaryId: Int, categoryId: Int) {
        summaryTextRepository.assignCategoryToSummary(summaryId, categoryId)
    }

    suspend fun executeGetSummariesByCategoryId(categoryId: Int): List<SummaryTextModel> {
        return summaryTextRepository.getSummariesByCategoryId(categoryId)
    }

    suspend fun executeDeleteSummaryText(summaryId: Int) {
        summaryTextRepository.deleteSummaryText(summaryId)
    }

    suspend fun executeGetSummaryCountByCategoryId(categoryId: Int): Int {
        return summaryTextRepository.getSummaryCountByCategoryId(categoryId)
    }

    suspend fun executeRemoveCategoryFromSummary(summaryId: Int, categoryId: Int) {
        summaryTextRepository.removeCategoryFromSummary(summaryId, categoryId)
    }

    suspend fun executeIsSummaryInCategory(summaryId: Int, categoryId: Int): Boolean {
        return summaryTextRepository.isSummaryInCategory(summaryId, categoryId)
    }

}

enum class SummaryStyle{
    STANDARD,
    ACADEMIC
}