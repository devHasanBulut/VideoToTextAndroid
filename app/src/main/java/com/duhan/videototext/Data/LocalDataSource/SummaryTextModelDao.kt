package com.duhan.videototext.Data.LocalDataSource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SummaryTextModelDao {

    @Query("SELECT * FROM summaryText")
    suspend fun getAllSummaryText(): List<SummaryTextModel>

    @Query("SELECT * FROM summaryText WHERE id = :id")
    suspend fun getSummaryTextById(id: Int): SummaryTextModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummaryText(summaryTextModel: SummaryTextModel)

    @Query("DELETE FROM summaryText")
    suspend fun clearAll()

    @Query("SELECT * FROM summarytext WHERE url = :url LIMIT 1")
    suspend fun getSummaryByUrl(url: String): SummaryTextModel?

    @Query("""
        SELECT st.* FROM summaryText as st
        INNER JOIN summaryForCategories as sc ON st.id = sc.summaryId
        WHERE sc.categoryId = :categoryId
    """)
    suspend fun getSummariesByCategoryId(categoryId: Int): List<SummaryTextModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSummaryCategoryCrossRef(crossRef: SummaryForCategories)

    @Query("DELETE FROM summaryText WHERE id = :summaryId")
    suspend fun deleteSummaryTextById(summaryId: Int)

    @Query("""
        SELECT COUNT(st.id) FROM summaryText as st
        INNER JOIN summaryForCategories as sc ON st.id = sc.summaryId
        WHERE sc.categoryId = :categoryId
    """)
    suspend fun getSummaryCountByCategoryId(categoryId: Int): Int

    @Query("DELETE FROM summaryForCategories WHERE summaryId = :summaryId AND categoryId = :categoryId")
    suspend fun removeCategoryFromSummary(summaryId: Int, categoryId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM summaryForCategories WHERE summaryId = :summaryId AND categoryId = :categoryId)")
    suspend fun isSummaryInCategory(summaryId: Int, categoryId: Int): Boolean

}
