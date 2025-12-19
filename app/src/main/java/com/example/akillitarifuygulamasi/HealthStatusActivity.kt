package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.entity.HealthStatusEntity
import com.example.akillitarifuygulamasi.ui.HealthChoiceAdapter
import com.example.akillitarifuygulamasi.ui.viewmodel.HealthStatusViewModel
import com.example.akillitarifuygulamasi.ui.viewmodel.UserViewModel

class HealthStatusActivity : AppCompatActivity() {

    private val userVm: UserViewModel by viewModels()
    private val healthVm: HealthStatusViewModel by viewModels()

    private lateinit var session: SessionManager
    private var selectedStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_status)

        // ✅ SessionManager هو المصدر الوحيد
        session = SessionManager(this)

        val editMode = intent.getBooleanExtra("EDIT_MODE", false)
        val userId = session.getUserId()

        // حماية
        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // منع الدخول المتكرر لو الحالة محفوظة (إلا في وضع التعديل)
        if (!editMode) {
            userVm.getUserById(userId) { user ->
                runOnUiThread {
                    if (user != null && user.healthStatus != "none") {
                        startActivity(Intent(this, SmartHomeActivity::class.java))
                        finish()
                    }
                }
            }
        }

        // RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rvHealth)
        rv.layoutManager = LinearLayoutManager(this)

        val adapter = HealthChoiceAdapter(emptyList()) { selectedName ->
            selectedStatus = selectedName
        }
        rv.adapter = adapter

        // Observe data
        healthVm.statuses.observe(this) { list: List<HealthStatusEntity> ->
            adapter.updateData(list)
        }

        healthVm.loadStatuses()

        // زر المتابعة
        val btnContinue = findViewById<Button>(R.id.continueButton)
        btnContinue.setOnClickListener {

            val status = selectedStatus
            if (status == null) {
                Toast.makeText(this, "Lütfen birini seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userVm.updateHealthStatus(userId, status) { ok ->
                runOnUiThread {
                    if (ok) {
                        // ✅ الحفظ الصحيح الوحيد
                        session.setHealthStatus(status)

                        if (editMode) {
                            finish()
                        } else {
                            startActivity(Intent(this, SmartHomeActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Güncelleme başarısız", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
