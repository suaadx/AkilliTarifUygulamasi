// app/src/main/java/com/example/akillitarifuygulamasi/data/model/RecipeWithRelations.kt
package com.example.akillitarifuygulamasi.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.akillitarifuygulamasi.data.entity.IngredientEntity
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity

data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(parentColumn = "id", entityColumn = "recipeId")
    val ingredients: List<IngredientEntity>
)
