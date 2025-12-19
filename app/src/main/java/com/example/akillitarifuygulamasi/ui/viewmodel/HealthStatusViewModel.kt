package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.HealthStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HealthStatusViewModel(application: Application) : AndroidViewModel(application) {

    val statuses = MutableLiveData<List<HealthStatusEntity>>()    // مراقبة الحالات الصحية

    private val dao = AppDatabase.getInstance(application).healthStatusDao()

    // تحميل جميع الحالات من قاعدة البيانات
    fun loadStatuses() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = dao.getAll()     // << تعمل مع suspend أو non-suspend
            withContext(Dispatchers.Main) {
                statuses.value = data
            }
        }
    }

    // إضافة حالة جديدة
    fun addStatus(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(HealthStatusEntity(name = name))
            loadStatuses()   // إعادة تحميل القائمة
        }
    }

    // حذف حالة
    fun deleteStatus(entity: HealthStatusEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(entity)
            loadStatuses()   // تحديث القائمة
        }
    }
}
