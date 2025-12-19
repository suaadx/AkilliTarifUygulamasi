package com.example.akillitarifuygulamasi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val hasSugar: Boolean,
    val hasSalt: Boolean,
    val isFried: Boolean
)
