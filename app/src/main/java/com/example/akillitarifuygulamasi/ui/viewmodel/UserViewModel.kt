package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.UserEntity
import com.example.akillitarifuygulamasi.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val dao = AppDatabase.getInstance(application).userDao()
        repository = UserRepository(dao)
    }

    /** 🔹 تسجيل مستخدم جديد */
    fun register(user: UserEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.register(user)
            withContext(Dispatchers.Main) { onResult(success) }
        }
    }

    /** 🔹 تسجيل الدخول */
    fun login(email: String, password: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.login(email, password)
            withContext(Dispatchers.Main) { onResult(user) }
        }
    }

    /** 🔹 جلب مستخدم بالـ id */
    fun getUserById(id: Int, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserById(id)
            withContext(Dispatchers.Main) { onResult(user) }
        }
    }

    /** 🔹 تحديث الحالة الصحية */
    fun updateHealthStatus(userId: Int, status: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserById(userId)
            if (user != null) {
                val updated = user.copy(healthStatus = status)
                repository.updateUser(updated)
                withContext(Dispatchers.Main) { onResult(true) }
            } else {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    /** 🔹 تحديث كامل بيانات المستخدم (اسم، بريد، كلمة مرور...) */
    fun updateUser(user: UserEntity, onResult: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
            withContext(Dispatchers.Main) { onResult() }
        }
    }

    /** 🔹 جلب اسم المستخدم حسب الـ ID (للتعليقات) */
    fun getUserName(id: Int, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserNameById(id)
            withContext(Dispatchers.Main) { onResult(user) }
        }
    }

}
