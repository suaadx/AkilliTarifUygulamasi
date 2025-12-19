package com.example.akillitarifuygulamasi
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.databinding.ActivityLogBinding
import com.example.akillitarifuygulamasi.ui.viewmodel.ActivityLogViewModel
import com.example.akillitarifuygulamasi.ui.ActivityLogAdapter


class ActivityLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding
    private val vm: ActivityLogViewModel by viewModels()
    private lateinit var adapter: ActivityLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ActivityLogAdapter()
        binding.recyclerViewActivityLog.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewActivityLog.adapter = adapter

        vm.activityList.observe(this) { list ->
            adapter.submitList(list)
        }

        vm.loadAll()
    }
}
