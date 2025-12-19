package com.example.akillitarifuygulamasi.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_activity")
data class UserActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val recipeId: Int?,
    val actionType: String,   // LIKE, COMMENT, DELETE_COMMENT...
    val details: String?,     // النص، أو معلومات إضافية
    val createdAt: Long = System.currentTimeMillis()
)
