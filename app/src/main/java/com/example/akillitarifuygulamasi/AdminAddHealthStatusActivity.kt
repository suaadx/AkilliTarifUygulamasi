package com.example.akillitarifuygulamasi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.akillitarifuygulamasi.ui.viewmodel.HealthStatusViewModel

class AdminAddHealthStatusActivity : AppCompatActivity() {

    private val vm: HealthStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_health_status)

        val et = findViewById<EditText>(R.id.etName)
        val save = findViewById<Button>(R.id.btnSave)

        save.setOnClickListener {
            val text = et.text.toString().trim()
            if (text.isNotEmpty()) {
                vm.addStatus(text)
                finish()
            }
        }
    }
}
