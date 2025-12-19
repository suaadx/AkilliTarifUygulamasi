package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.FavoriteEntity
import com.example.akillitarifuygulamasi.data.entity.UserActivityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.favoriteDao()
    private val activityDao = db.userActivityDao()   // ← إضافة DAO النشاطات

    fun toggleFavorite(recipeId: Int, userId: Int, onDone: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {

            val exists = dao.isFavorite(recipeId, userId) > 0

            if (exists) {
                // حذف من المفضلة
                dao.removeByIds(recipeId, userId)

                // تسجيل نشاط إلغاء لايك
                activityDao.insert(
                    UserActivityEntity(
                        userId = userId,
                        recipeId = recipeId,
                        actionType = "UNLIKE",
                        details = null
                    )
                )

                onDone(false)

            } else {
                // إضافة للمفضلة
                dao.add(FavoriteEntity(recipeId = recipeId, userId = userId))

                // تسجيل نشاط لايك
                activityDao.insert(
                    UserActivityEntity(
                        userId = userId,
                        recipeId = recipeId,
                        actionType = "LIKE",
                        details = null
                    )
                )

                onDone(true)
            }
        }
    }

    fun isFavorite(recipeId: Int, userId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val exists = dao.isFavorite(recipeId, userId) > 0
            callback(exists)
        }
    }

    fun removeFavorite(userId: Int, recipeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.removeFavorite(userId, recipeId)

            // تسجيل النشاط
            activityDao.insert(
                UserActivityEntity(
                    userId = userId,
                    recipeId = recipeId,
                    actionType = "UNLIKE",
                    details = "Removed from favorites"
                )
            )
        }
    }

    fun getFavorites(userId: Int): LiveData<List<FavoriteEntity>> {
        return dao.getFavorites(userId)
    }
}
