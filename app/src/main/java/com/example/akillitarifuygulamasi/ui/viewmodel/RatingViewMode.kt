package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.RatingEntity
import com.example.akillitarifuygulamasi.data.entity.UserActivityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RatingViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val ratingDao = db.ratingDao()
    private val activityDao = db.userActivityDao()   // ← إضافة DAO للنشاطات

    fun rate(userId: Int, recipeId: Int, stars: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            // 🟡 حفظ / تحديث التقييم
            ratingDao.upsert(
                RatingEntity(
                    recipeId = recipeId,
                    userId = userId,
                    stars = stars
                )
            )

            // 🟢 تسجيل نشاط RATING
            activityDao.insert(
                UserActivityEntity(
                    userId = userId,
                    recipeId = recipeId,
                    actionType = "RATING",
                    details = "Rating = $stars"
                )
            )
        }
    }

    fun observeAverage(recipeId: Int): LiveData<Double?> {
        return ratingDao.getAverageForRecipe(recipeId)
    }

    fun getUserRating(userId: Int, recipeId: Int, onResult: (Int?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val rating = ratingDao.getUserRating(recipeId, userId)
            onResult(rating?.stars)
        }
    }

    fun getCount(recipeId: Int): LiveData<Int> {
        return ratingDao.getCountForRecipe(recipeId)
    }
}
