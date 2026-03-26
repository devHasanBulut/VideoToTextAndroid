package com.duhan.videototext.Data.LocalDataSource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoriesModel>

    @Query("SELECT * FROM categories WHERE categoryName = :categoryName")
    suspend fun getCategoryByName(categoryName: String): CategoriesModel?

    @Query("DELETE FROM categories WHERE categoryName = :categoryName")
    suspend fun deleteCategoryByName(categoryName: String)

    @Insert
   suspend fun insertCategory(category: CategoriesModel)

   @Query("SELECT * FROM categories WHERE id = :id")
   suspend fun getCategoryByID(id: Int): CategoriesModel?

}