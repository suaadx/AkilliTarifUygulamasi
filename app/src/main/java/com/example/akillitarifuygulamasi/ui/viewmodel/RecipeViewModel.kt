package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.ai.AIRecommendationEngine
import com.example.akillitarifuygulamasi.data.ai.HealthTag
import com.example.akillitarifuygulamasi.data.ai.TextNormalizer
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.data.entity.RecipeWithIngredients
import com.example.akillitarifuygulamasi.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.akillitarifuygulamasi.data.ai.HealthGate
import kotlinx.coroutines.withContext

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    val allRecipes: LiveData<List<RecipeEntity>>

    init {
        val db = AppDatabase.getInstance(application)
        repository = RecipeRepository(
            recipeDao = db.recipeDao(),
            ingredientDao = db.ingredientDao(),
            userActivityDao = db.userActivityDao(),
            favoriteDao = db.favoriteDao(),
            ratingDao = db.ratingDao()
        )
        allRecipes = repository.allRecipes
    }

    // ----------------------------------------------------
    // ⭐ Helpers
    // ----------------------------------------------------
    fun getByIds(ids: List<Int>): LiveData<List<RecipeEntity>> =
        repository.getByIds(ids)

    fun getRecipeWithIngredients(recipeId: Int): LiveData<RecipeWithIngredients> =
        repository.getRecipeWithIngredients(recipeId)

    fun search(query: String) = repository.search(query)

    // ----------------------------------------------------
    // ⭐ SMART AI RECOMMENDATIONS (FINAL & SAFE)
    // ----------------------------------------------------
    fun getSmartRecommendations(
        userId: Int,
        userHealth: HealthTag?,
        mealFilter: List<String>?,
        onResult: (List<RecipeEntity>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            // 🛑 أمان: لو userId غير صالح رجّع نتائج عامة
            if (userId <= 0) {
                val fallback = repository.getAllNow().shuffled().take(8)
                withContext(Dispatchers.Main) {
                    onResult(fallback)
                }
                return@launch
            }

            // 1) جميع الوصفات
            val allRecipes = repository.getAllNow()

            // 2) فلترة صحية
            // 2) فلترة صحية حقيقية (HealthGate)
            val healthFilteredRecipes = allRecipes.filter { recipe ->

                val result = HealthGate.check(
                    textRaw = recipe.title + " " + recipe.description,
                    userHealthTags = userHealth?.let { setOf(it) } ?: emptySet()
                )

                if (!result.allowed) {
                    android.util.Log.d(
                        "AI_FILTER",
                        "REJECT ${recipe.title} | ${result.reason}"
                    )
                }

                result.allowed
            }


            // 3) المكونات
            val ingredientsMap: Map<Int, List<String>> =
                repository.getAllIngredients().mapValues { entry ->
                    entry.value.map { it.name }
                }

            // 4) نشاط المستخدم
            val viewedIds = repository.getViewedRecipes(userId).toSet()
            val favoriteIds = repository.getFavoriteRecipeIds(userId).toSet()
            val ratedStars = repository.getUserRatings(userId)
                .associate { it.recipeId to it.stars }

            // 5) meal filter
            val normalizedMeals =
                mealFilter?.map { TextNormalizer.normalize(it) }?.toSet()

            // 6) AI Engine
            val result = AIRecommendationEngine.recommend(
                allRecipes = healthFilteredRecipes,
                ingredientsMap = ingredientsMap,
                userHealth = userHealth,
                viewedIds = viewedIds,
                favoriteIds = favoriteIds,
                ratedStars = ratedStars,
                mealFilter = normalizedMeals,
                idOf = { it.id },
                titleOf = { it.title },
                mealOf = { it.meal },
                descriptionOf = { it.description }
            )

            // 7) ضمان عدم الفراغ
            val safeResult =
                if (result.isEmpty()) {
                    healthFilteredRecipes.shuffled().take(8)
                } else {
                    result
                }

            // ✅ دائمًا رجّع النتيجة على Main Thread
            withContext(Dispatchers.Main) {
                onResult(safeResult)
            }
        }
    }
}
