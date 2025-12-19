package com.example.akillitarifuygulamasi

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import com.example.akillitarifuygulamasi.data.entity.UserEntity
import com.example.akillitarifuygulamasi.ui.viewmodel.UserViewModel

class RegisterActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullName = findViewById<EditText>(R.id.fullNameEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val confirmPassword = findViewById<EditText>(R.id.confirmPasswordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginLink = findViewById<TextView>(R.id.loginLink)

        // رجوع لصفحة تسجيل الدخول
        loginLink.setOnClickListener { finish() }

        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val db = com.example.akillitarifuygulamasi.data.AppDatabase.getInstance(this@RegisterActivity)
            val recipeDao = db.recipeDao()



            // (اختياري) تأكيد سريع: لو ودّك تقرأ عدد الوصفات
            // val all = recipeDao.getAll().value  // هذا LiveData؛ راقبه في شاشة القائمة
        }

        registerButton.setOnClickListener {
            val nameStr = fullName.text.toString().trim()
            val emailStr = email.text.toString().trim()
            val passStr = password.text.toString().trim()
            val confirmStr = confirmPassword.text.toString().trim()

            // تحقق بسيط من الحقول
            if (nameStr.isEmpty() || emailStr.isEmpty() || passStr.isEmpty() || confirmStr.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passStr.length < 6) {
                Toast.makeText(this, "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passStr != confirmStr) {
                Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // healthStatus حالياً نخليه "none" إلى أن تربطه بشاشة اختيار الحالة الصحية
            val user = UserEntity(
                name = nameStr,
                email = emailStr,
                password = passStr,
                healthStatus = "none"
            )

            // استدعاء التسجيل عبر ViewModel + Room
            viewModel.register(user) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Kayıt başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_SHORT).show()
                        finish() // نرجع لشاشة LoginActivity
                    } else {
                        Toast.makeText(this, "Bu e-posta zaten kayıtlı", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
