package com.example.akillitarifuygulamasi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.databinding.ActivityUserActivityLogBinding
import com.example.akillitarifuygulamasi.UserActivityLogActivity
import com.example.akillitarifuygulamasi.ui.viewmodel.UserActivityLogViewModel
import com.example.akillitarifuygulamasi.ui.UserActivityAdapter



class UserActivityLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserActivityLogBinding
    private val viewModel: UserActivityLogViewModel by viewModels()
    private lateinit var adapter: UserActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserActivityAdapter()
        binding.recyclerUserActivity.layoutManager = LinearLayoutManager(this)
        binding.recyclerUserActivity.adapter = adapter

        val userId = intent.getIntExtra("userId", -1)

        // تحميل نشاط المستخدم
        viewModel.loadUserActivity(userId)

        // مراقبة البيانات
        viewModel.activities.observe(this) { list ->
            adapter.submitList(list)
        }
    }

}
