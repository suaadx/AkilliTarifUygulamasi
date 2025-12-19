package com.example.akillitarifuygulamasi

data class RecipeModel(
    val name: String,
    val imageResId: Int,
    val rating: Float,
    val ingredients: String,
    val instructions: String,
    val calories: Int,
    val mealType: String,              // <-- مهم جداً
)
