package com.example.akillitarifuygulamasi.data.entity

data class CommentWithUserAndRecipe(
    val id: Int,
    val userId: Int,
    val recipeId: Int,
    val text: String,
    val createdAt: Long,
    val userName: String,
    val recipeTitle: String
)
