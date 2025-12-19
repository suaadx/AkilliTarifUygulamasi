package com.example.akillitarifuygulamasi.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.akillitarifuygulamasi.data.entity.IngredientEntity

@Dao
interface IngredientDao {

    // المستخدم العادي يشوف LiveData
    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId ORDER BY id ASC")
    fun getByRecipe(recipeId: Int): LiveData<List<IngredientEntity>>

    // الأدمن يحتاج نسخة مباشرة (بدون LiveData)
    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId ORDER BY id ASC")
    suspend fun getByRecipeNow(recipeId: Int): List<IngredientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(item: IngredientEntity): Long

    @Update
    suspend fun update(item: IngredientEntity)

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteByRecipe(recipeId: Int)

    @Query("SELECT * FROM ingredients")
    suspend fun getAll(): List<IngredientEntity>

}
