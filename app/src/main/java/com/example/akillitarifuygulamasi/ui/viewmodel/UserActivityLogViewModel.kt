package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.ActivityDisplay
import com.example.akillitarifuygulamasi.data.AppDatabase
import kotlinx.coroutines.launch

class UserActivityLogViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val activityDao = db.userActivityDao()
    private val recipeDao = db.recipeDao()

    val activities = MutableLiveData<List<ActivityDisplay>>()

    fun loadUserActivity(userId: Int) {
        viewModelScope.launch {

            val list = activityDao.getUserActivity(userId)

            val mapped = list.map { item ->

                val recipeTitle = item.recipeId?.let {
                    recipeDao.getRecipeTitleById(it)
                } ?: "Bilinmeyen Tarif"

                // ---- صياغة نص مخصص قابل للتنسيق ----
                val actionTitle = when (item.actionType) {
                    "COMMENT"  -> "Yorum Yapıldı"
                    "DELETE_COMMENT" -> "Yorum Silindi"
                    "LIKE" -> "Beğenilen Tarif"
                    "UNLIKE" -> "Beğeni Kaldırıldı"
                    "RATING" -> "Puan Verildi"
                    else -> item.actionType
                }

                val details = when (item.actionType) {
                    "COMMENT" -> item.details            // نص التعليق
                    "DELETE_COMMENT" -> item.details
                    "RATING" -> "Rating = ${item.details}"
                    else -> null                         // إعجاب/إزالة إعجاب ما له تفاصيل
                }

                ActivityDisplay(
                    action = actionTitle,
                    details = details,
                    recipeTitle = recipeTitle,
                    type = item.actionType,
                    date = item.createdAt
                )
            }

            activities.postValue(mapped)
        }
    }
}
