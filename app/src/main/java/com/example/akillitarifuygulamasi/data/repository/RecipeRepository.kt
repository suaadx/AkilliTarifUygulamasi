package com.example.akillitarifuygulamasi.data.repository

import androidx.lifecycle.LiveData
import com.example.akillitarifuygulamasi.data.dao.RecipeDao
import com.example.akillitarifuygulamasi.data.dao.UserActivityDao
import com.example.akillitarifuygulamasi.data.dao.RatingDao
import com.example.akillitarifuygulamasi.data.dao.FavoriteDao
import com.example.akillitarifuygulamasi.data.dao.IngredientDao
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.data.entity.RatingEntity
import com.example.akillitarifuygulamasi.data.entity.IngredientEntity
import com.example.akillitarifuygulamasi.data.entity.RecipeWithIngredients

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val userActivityDao: UserActivityDao,
    private val favoriteDao: FavoriteDao,
    private val ratingDao: RatingDao
)
 {

    val allRecipes: LiveData<List<RecipeEntity>> = recipeDao.getAll()

    fun getByHealthTag(tag: String): LiveData<List<RecipeEntity>> =
        recipeDao.getByHealthTag(tag)

    fun search(query: String): LiveData<List<RecipeEntity>> =
        recipeDao.searchByTitle(query)

    suspend fun insert(recipe: RecipeEntity) = recipeDao.insert(recipe)

    suspend fun update(recipe: RecipeEntity) = recipeDao.update(recipe)

    suspend fun delete(recipe: RecipeEntity) = recipeDao.delete(recipe)

    fun getByIds(ids: List<Int>): LiveData<List<RecipeEntity>> =
        recipeDao.getByIds(ids)

    // ⭐ دالة جلب وصفات حسب الوجبة فقط
    fun getByMeal(meal: String): LiveData<List<RecipeEntity>> =
        recipeDao.getByMeal(meal)

    // ⭐ دالة الفلترة (نفس المطلوبة من RecommendedRecipes)
    fun getByFilters(
        healthTags: List<String>,
        meals: List<String>
    ): LiveData<List<RecipeEntity>> {

        val healthEmpty = if (healthTags.isEmpty()) 1 else 0
        val mealsEmpty = if (meals.isEmpty()) 1 else 0

        return recipeDao.getByFilters(
            healthTags,
            meals,
            healthEmpty,
            mealsEmpty
        )
    }

    suspend fun getAllIngredients(): Map<Int, List<IngredientEntity>> {
        val list = ingredientDao.getAll()
        return list.groupBy { it.recipeId }
    }

    suspend fun getViewedRecipes(userId: Int): List<Int> =
        userActivityDao.getViewedIds(userId)

    suspend fun getFavoriteRecipeIds(userId: Int): List<Int> =
        favoriteDao.getUserFavoritesIds(userId)

    suspend fun getRatedRecipeIds(userId: Int): List<Int> =
        ratingDao.getRatedRecipeIds(userId)

    // ⭐ هذه هي الدالة الصحيحة لـ AdminManageRecipes
    suspend fun getAllNow(): List<RecipeEntity> =
        recipeDao.getAllNow()

     suspend fun getUserRatings(userId: Int): List<RatingEntity> {
         return ratingDao.getUserRatings(userId)
     }

     fun getRecipeWithIngredients(recipeId: Int): LiveData<RecipeWithIngredients> {
         return recipeDao.getRecipeWithIngredients(recipeId)
     }


 }
