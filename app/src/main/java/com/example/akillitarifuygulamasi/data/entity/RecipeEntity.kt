package com.example.akillitarifuygulamasi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val imageResId: Int?,          // رابط/اسم صورة محلية؛ nullable
    val calories: Int,
    val meal: String,
    val healthTag: String? = null,
    val imagePath: String? = null// لتصفية الوصفات حسب الحالة الصحية (مثال: "diabetes")
)
