package com.example.akillitarifuygulamasi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.UserActivityEntity
import kotlinx.coroutines.launch

class ActivityLogViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).userActivityDao()

    val activityList = MutableLiveData<List<UserActivityEntity>>()

    fun loadAll() {
        viewModelScope.launch {
            activityList.postValue(dao.getAllActivity())
        }
    }

    fun loadByUser(userId: Int) {
        viewModelScope.launch {
            activityList.postValue(dao.getUserActivity(userId))
        }
    }
}
