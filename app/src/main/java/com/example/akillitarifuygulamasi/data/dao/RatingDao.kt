package com.example.akillitarifuygulamasi.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.akillitarifuygulamasi.data.entity.RatingEntity
import com.example.akillitarifuygulamasi.data.model.RatingSummary

@Dao
interface RatingDao {

    // يضيف أو يحدّث تقييم المستخدم
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rating: RatingEntity): Long

    // جلب تقييم مستخدم معين لوصفة معيّنة
    @Query("SELECT * FROM ratings WHERE recipeId = :recipeId AND userId = :userId LIMIT 1")
    suspend fun getUserRating(recipeId: Int, userId: Int): RatingEntity?

    // متوسط التقييم لوصفة واحدة
    @Query("SELECT AVG(stars) FROM ratings WHERE recipeId = :recipeId")
    fun getAverageForRecipe(recipeId: Int): LiveData<Double?>

    // عدد المقيّمين لوصفة واحدة
    @Query("SELECT COUNT(*) FROM ratings WHERE recipeId = :recipeId")
    fun getCountForRecipe(recipeId: Int): LiveData<Int>

    // 🔥 ملخص التقييمات (Top Rated)
    @Query("""
        SELECT 
            recipeId AS recipeId,
            AVG(stars) AS avgRating,
            COUNT(*) AS ratingCount
        FROM ratings
        GROUP BY recipeId
        HAVING ratingCount > 0
        ORDER BY avgRating DESC
    """)
    suspend fun getAllRatingsSummary(): List<RatingSummary>

    // الوصفات التي قيّمها المستخدم (قديمة – نستخدمها أحيانًا)
    @Query("SELECT recipeId FROM ratings WHERE userId = :userId")
    suspend fun getRatedRecipeIds(userId: Int): List<Int>

    // ⭐⭐⭐ الدالة المطلوبة للـ AI (stars-based)
    @Query("SELECT * FROM ratings WHERE userId = :userId")
    suspend fun getUserRatings(userId: Int): List<RatingEntity>
}
