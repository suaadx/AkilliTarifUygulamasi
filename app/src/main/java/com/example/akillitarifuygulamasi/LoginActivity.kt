package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.akillitarifuygulamasi.databinding.ActivityLoginBinding
import com.example.akillitarifuygulamasi.ui.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userVm: UserViewModel by viewModels()

    // ✳️ بيانات الأدمن الهاردكود
    private val ADMIN_EMAIL = "admin@gmail.com"
    private val ADMIN_PASSWORD = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        // ✅ Auto-login فقط إذا المستخدم فعليًا مسجّل دخول
        if (session.isLoggedIn()) {
            val savedId = session.getUserId()
            if (savedId != -1) {
                userVm.getUserById(savedId) { user ->
                    if (user != null) {
                        val intent = Intent(this, SmartHomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                return
            }
        }

        // زر إنشاء حساب
        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // زر تسجيل الدخول
        binding.loginButton.setOnClickListener {

            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 دخول الأدمن
            if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
                Toast.makeText(this, "Admin girişi başarılı", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, AdminHomeActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                return@setOnClickListener
            }

            // 🔹 دخول المستخدم العادي
            userVm.login(email, password) { user ->
                runOnUiThread {

                    if (user == null) {
                        Toast.makeText(
                            this,
                            "Geçersiz e-posta veya şifre",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@runOnUiThread
                    }

                    if (user.status == "suspended") {
                        Toast.makeText(
                            this,
                            "Hesabınız durduruldu!",
                            Toast.LENGTH_LONG
                        ).show()
                        return@runOnUiThread
                    }

                    // ✅ حفظ الجلسة
                    session.saveUser(
                        user.id,
                        user.email,
                        user.name)
                    session.setHealthStatus(user.healthStatus)


                    Toast.makeText(
                        this,
                        "Hoş geldin ${user.name}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = if (user.healthStatus == "none") {
                        Intent(this, HealthStatusActivity::class.java)
                            .putExtra("user_id", user.id)
                    } else {
                        Intent(this, SmartHomeActivity::class.java)
                    }

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }
}
