package com.example.akillitarifuygulamasi.data.dao

import com.example.akillitarifuygulamasi.data.entity.RecipeWithIngredients
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    suspend fun getAllNow(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE healthTag = :tag ORDER BY id DESC")
    fun getByHealthTag(tag: String): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchByTitle(query: String): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id IN (:ids)")
    fun getByIds(ids: List<Int>): LiveData<List<RecipeEntity>>

    // ⭐ فلترة الوجبة فقط
    @Query("SELECT * FROM recipes WHERE LOWER(meal) = LOWER(:meal) ORDER BY id DESC")
    fun getByMeal(meal: String): LiveData<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity): Long

    @Update
    suspend fun update(recipe: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): RecipeEntity?

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredients(recipeId: Int): LiveData<RecipeWithIngredients>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<RecipeEntity>)

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun count(): Int

    @Query("SELECT title FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getRecipeTitleById(id: Int): String?

    @Query("""
        SELECT * FROM recipes
        WHERE (:healthEmpty = 1 OR LOWER(healthTag) IN (:healthTags))
          AND (:mealsEmpty  = 1 OR LOWER(meal)      IN (:meals))
        ORDER BY id DESC
    """)
    fun getByFilters(
        healthTags: List<String>,
        meals: List<String>,
        healthEmpty: Int,
        mealsEmpty: Int
    ): LiveData<List<RecipeEntity>>

    // ⭐ أعلى الوصفات تقييماً
    @Query("""
        SELECT recipes.* FROM recipes
        INNER JOIN ratings ON ratings.recipeId = recipes.id
        GROUP BY recipes.id
        ORDER BY AVG(ratings.stars) DESC, COUNT(ratings.userId) DESC
        LIMIT :limit
    """)
    suspend fun getTopRated(limit: Int): List<RecipeEntity>


    // ⭐ أكثر الوصفات إضافة للمفضلة
    @Query("""
        SELECT recipes.* FROM recipes
        INNER JOIN favorites ON favorites.recipeId = recipes.id
        GROUP BY recipes.id
        ORDER BY COUNT(favorites.userId) DESC
        LIMIT :limit
    """)
    suspend fun getMostFavorited(limit: Int): List<RecipeEntity>
}
