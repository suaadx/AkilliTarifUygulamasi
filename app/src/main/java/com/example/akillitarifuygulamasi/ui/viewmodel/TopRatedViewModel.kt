package com.example.akillitarifuygulamasi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.model.TopRatedRecipeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopRatedViewModel(private val db: AppDatabase) : ViewModel() {

    private val _topRated = MutableStateFlow<List<TopRatedRecipeItem>>(emptyList())
    val topRated: StateFlow<List<TopRatedRecipeItem>> get() = _topRated

    fun loadTopRated() {
        viewModelScope.launch(Dispatchers.IO) {

            val summaries = db.ratingDao().getAllRatingsSummary()
            val finalList = mutableListOf<TopRatedRecipeItem>()

            for (summary in summaries) {
                val recipe = db.recipeDao().getById(summary.recipeId)
                if (recipe != null) {
                    finalList.add(
                        TopRatedRecipeItem(
                            recipeId = recipe.id,
                            title = recipe.title,
                            avgRating = summary.avgRating,
                            ratingCount = summary.ratingCount
                        )
                    )
                }
            }

            _topRated.value = finalList
        }
    }
}
