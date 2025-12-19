package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.ui.HealthStatusAdapter
import com.example.akillitarifuygulamasi.ui.viewmodel.HealthStatusViewModel

class AdminManageHealthActivity : AppCompatActivity() {

    private val vm: HealthStatusViewModel by viewModels()
    private lateinit var adapter: HealthStatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_health)

        val rv = findViewById<RecyclerView>(R.id.rvHealth)
        val btnAdd = findViewById<Button>(R.id.btnAddHealth)

        adapter = HealthStatusAdapter(
            onDelete = { vm.deleteStatus(it) }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        vm.statuses.observe(this) { list ->
            adapter.submitList(list)
        }

        vm.loadStatuses()

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AdminAddHealthStatusActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        vm.loadStatuses()
    }
}
