package com.example.akillitarifuygulamasi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_status")
data class HealthStatusEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
