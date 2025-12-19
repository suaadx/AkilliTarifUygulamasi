package com.example.akillitarifuygulamasi.data.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.akillitarifuygulamasi.data.entity.UserEntity
import com.example.akillitarifuygulamasi.data.entity.UserActivityEntity


@Dao
interface UserActivityDao {

    @Insert
    suspend fun insert(activity: UserActivityEntity)

    @Query("SELECT * FROM user_activity ORDER BY createdAt DESC")
    suspend fun getAllActivity(): List<UserActivityEntity>

    @Query("SELECT * FROM user_activity WHERE userId = :id ORDER BY createdAt DESC")
    suspend fun getUserActivity(id: Int): List<UserActivityEntity>

    @Query("SELECT recipeId FROM user_activity WHERE userId = :userId")
    suspend fun getViewedIds(userId: Int): List<Int>

}
