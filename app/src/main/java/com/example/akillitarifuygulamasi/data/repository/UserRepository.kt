package com.example.akillitarifuygulamasi.data.repository

import com.example.akillitarifuygulamasi.data.dao.UserDao
import com.example.akillitarifuygulamasi.data.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    // تسجيل مستخدم جديد
    suspend fun register(user: UserEntity): Boolean {
        val existing = userDao.getUserByEmail(user.email)
        return if (existing == null) {
            userDao.insertUser(user)
            true
        } else false
    }

    // تسجيل الدخول
    suspend fun login(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }

    // تحديث بيانات المستخدم
    suspend fun updateUser(user: UserEntity) = userDao.update(user)

    // حذف المستخدم
    suspend fun deleteUser(user: UserEntity) = userDao.delete(user)

    // جلب المستخدم حسب المعرّف
    suspend fun getUserById(id: Int): UserEntity? = userDao.getById(id)

    // جلب المستخدم حسب الإيميل
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    // جلب جميع المستخدمين
    fun getAllUsers() = userDao.getAll()

    // ⭐⭐ الدالة المطلوبة لعرض اسم المستخدم في التعليقات
    suspend fun getUserNameById(id: Int): String {
        return userDao.getById(id)?.name ?: "Kullanıcı"
    }
}
