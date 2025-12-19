package com.example.akillitarifuygulamasi.data

import com.example.akillitarifuygulamasi.data.dao.RatingDao
import com.example.akillitarifuygulamasi.data.dao.RecipeDao

import com.example.akillitarifuygulamasi.data.entity.RatingEntity
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity

import androidx.lifecycle.LiveData


class RatingRepository(private val dao: RatingDao) {

    suspend fun rate(userId: Int, recipeId: Int, stars: Int) {
        val rating = RatingEntity(
            userId = userId,
            recipeId = recipeId,
            stars = stars
        )
        dao.upsert(rating)
    }

    suspend fun getUserRating(userId: Int, recipeId: Int): RatingEntity? {
        return dao.getUserRating(recipeId, userId)
    }

    fun getAverage(recipeId: Int) = dao.getAverageForRecipe(recipeId)

    fun getCount(recipeId: Int) = dao.getCountForRecipe(recipeId)



}
