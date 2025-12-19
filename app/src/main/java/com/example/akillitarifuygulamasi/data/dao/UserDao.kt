package com.example.akillitarifuygulamasi.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.akillitarifuygulamasi.data.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clear()

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAll(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?
    // لتسجيل الدخول المحلي المؤقت (تحقق بسيط)
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int

    @Query("SELECT * FROM users ORDER BY name ASC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Int)

    @Query("UPDATE users SET role = :role WHERE id = :id")
    suspend fun updateRole(id: Int, role: String)

    @Query("UPDATE users SET status = :newStatus WHERE id = :id")
    fun updateStatus(id: Int, newStatus: String)

}
