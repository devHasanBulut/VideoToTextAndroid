package com.duhan.videototext.Domain

import com.duhan.videototext.Data.LocalDataSource.CategoriesModel
import com.duhan.videototext.Repository.CategoriesRepository
import javax.inject.Inject

class Categories @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    suspend fun getAllCategories(): List<Category> = categoriesRepository.getAllCategories()

    suspend fun insertCategory(categoryName: String) {
        categoriesRepository.insertCategory(categoryName)
    }

    suspend fun deleteCategory(categoryName: String) {
        categoriesRepository.deleteCategory(categoryName)
    }

    suspend fun getCategoryByName(categoryName: String): Category?{
       return categoriesRepository.getCategoryByName(categoryName)
    }

    suspend fun initDefaultCategories() {
        categoriesRepository.initDefaultCategories()
    }

}
data class Category(
    val id: Int,
    val categoryName: String,
)

fun CategoriesModel.toDomain(): Category {
    return Category(
        id = this.id,
        categoryName = this.categoryName
    )
}





