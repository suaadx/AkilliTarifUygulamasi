package com.example.akillitarifuygulamasi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.akillitarifuygulamasi.data.entity.HealthStatusEntity

@Dao
interface HealthStatusDao {

    @Query("SELECT * FROM health_status")
    suspend fun getAll(): List<HealthStatusEntity>

    @Insert
    suspend fun insert(status: HealthStatusEntity)

    @Delete
    suspend fun delete(status: HealthStatusEntity)
}
