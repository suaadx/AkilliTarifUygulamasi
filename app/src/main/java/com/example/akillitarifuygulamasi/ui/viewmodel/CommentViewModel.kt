package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.CommentEntity
import com.example.akillitarifuygulamasi.data.entity.UserActivityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.commentDao()
    private val activityDao = db.userActivityDao()   // ← إضافة DAO النشاط

    fun getComments(recipeId: Int): LiveData<List<CommentEntity>> {
        return dao.getByRecipe(recipeId)
    }

    fun addComment(recipeId: Int, userId: Int, text: String) {
        viewModelScope.launch(Dispatchers.IO) {

            // إضافة الكومنت
            dao.insert(
                CommentEntity(
                    recipeId = recipeId,
                    userId = userId,
                    text = text
                )
            )

            // تسجيل نشاط COMMENT
            activityDao.insert(
                UserActivityEntity(
                    userId = userId,
                    recipeId = recipeId,
                    actionType = "COMMENT",
                    details = text
                )
            )
        }
    }

    fun deleteComment(comment: CommentEntity, adminId: Int? = null) {
        viewModelScope.launch(Dispatchers.IO) {

            // حذف الكومنت
            dao.delete(comment)

            // تسجيل نشاط DELETE_COMMENT
            activityDao.insert(
                UserActivityEntity(
                    userId = adminId ?: comment.userId,  // إذا الأدمن حذف، نسجل الأدمن
                    recipeId = comment.recipeId,
                    actionType = "DELETE_COMMENT",
                    details = "Deleted comment: ${comment.text}"
                )
            )
        }
    }
}
