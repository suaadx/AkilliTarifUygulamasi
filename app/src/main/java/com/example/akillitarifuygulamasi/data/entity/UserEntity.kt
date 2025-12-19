package com.example.akillitarifuygulamasi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo


@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val email: String,
    val password: String,

    // user = مستخدم عادي
    // admin = مدير النظام
    val role: String = "user",

    // الحالة الصحية للمستخدم
    val healthStatus: String = "none",

    @ColumnInfo(name = "status")
     var status: String = "active"   // active or suspended

)
