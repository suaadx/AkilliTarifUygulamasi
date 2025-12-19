package com.example.akillitarifuygulamasi.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.akillitarifuygulamasi.data.entity.CommentEntity
import com.example.akillitarifuygulamasi.data.entity.CommentWithUserAndRecipe

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE recipeId = :recipeId ORDER BY createdAt DESC")
    fun getByRecipe(recipeId: Int): LiveData<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity): Long

    @Delete
    suspend fun delete(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE recipeId = :recipeId")
    suspend fun deleteByRecipe(recipeId: Int)

    @Query("SELECT COUNT(*) FROM comments")
    suspend fun countComments(): Int

    @Query("SELECT COUNT(*) FROM comments")
    fun getTotalComments(): LiveData<Int>

    // ================================
    //  🔥 Admin: Get all comments + user + recipe title
    // ================================
    @Query("""
        SELECT 
            comments.id AS id,
            comments.userId AS userId,
            comments.recipeId AS recipeId,
            comments.text AS text,
            comments.createdAt AS createdAt,
            users.name AS userName,
            recipes.title AS recipeTitle
        FROM comments
        INNER JOIN users 
            ON users.id = comments.userId
        INNER JOIN recipes 
            ON recipes.id = comments.recipeId
        ORDER BY comments.id DESC
    """)
    suspend fun getAllCommentsAdmin(): List<CommentWithUserAndRecipe>

    // ================================
    //  🔥 Admin: Delete single comment by ID
    // ================================
    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteById(commentId: Int)
}
