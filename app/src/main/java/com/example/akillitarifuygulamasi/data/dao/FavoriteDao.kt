package com.example.akillitarifuygulamasi.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.akillitarifuygulamasi.data.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(fav: FavoriteEntity): Long

    // حذف المفضلة بحسب userId + recipeId
    @Query("DELETE FROM favorites WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun removeFavorite(userId: Int, recipeId: Int)

    // حذف المفضلة لكن بترتيب مختلف (يستخدم في toggleFavorite)
    @Query("DELETE FROM favorites WHERE recipeId = :recipeId AND userId = :userId")
    suspend fun removeByIds(recipeId: Int, userId: Int)

    // جلب جميع مفضلات مستخدم
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavorites(userId: Int): LiveData<List<FavoriteEntity>>

    // هل الوصفة موجودة بالمفضلة؟
    @Query("SELECT COUNT(*) FROM favorites WHERE recipeId = :recipeId AND userId = :userId")
    suspend fun isFavorite(recipeId: Int, userId: Int): Int

    @Query("SELECT recipeId FROM favorites WHERE userId = :userId")
    suspend fun getUserFavoritesIds(userId: Int): List<Int>

}
