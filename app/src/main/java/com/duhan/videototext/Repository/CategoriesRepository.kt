package com.duhan.videototext.Repository

import com.duhan.videototext.Data.LocalDataSource.CategoriesDao
import com.duhan.videototext.Data.LocalDataSource.CategoriesModel
import com.duhan.videototext.Domain.Category
import com.duhan.videototext.Domain.toDomain
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao,

) {
    private val FAVORITES_ID = 1
    private val DOWNLOADS_ID = 2

    suspend fun getAllCategories(): List<Category>{
        return categoriesDao.getAllCategories().map { it.toDomain() }

    }
    suspend fun insertCategory(categoryName: String){
         categoriesDao.insertCategory(CategoriesModel(categoryName = categoryName)
        )
    }
    suspend fun deleteCategory(categoryName: String) {
        categoriesDao.deleteCategoryByName(categoryName)
    }

    suspend fun getCategoryByName(categoryName: String): Category? {
        return categoriesDao.getCategoryByName(categoryName)?.toDomain()
    }
    suspend fun initDefaultCategories() {
        // 1. Favoriler Var mı? (ID ile kontrol etmek en doğrusu)
        val favorites = categoriesDao.getCategoryByID(FAVORITES_ID)
        if (favorites == null) {
            // Yoksa, ID'si 1 olacak şekilde zorla ekle
            categoriesDao.insertCategory(
                CategoriesModel(
                    id = FAVORITES_ID,
                    categoryName = "Favorites" // Veya "Favoriler"
                )
            )
        }
        val downloads = categoriesDao.getCategoryByID(DOWNLOADS_ID)
        if (downloads == null) {
            // Yoksa, ID'si 2 olacak şekilde zorla ekle
            categoriesDao.insertCategory(
                CategoriesModel(
                    id = DOWNLOADS_ID,
                    categoryName = "Downloads" // Veya "İndirilenler"
                )
            )
        }
    }

}