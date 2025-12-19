package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.akillitarifuygulamasi.ui.viewmodel.UserViewModel

class SplashActivity : AppCompatActivity() {

    private val userVm: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // نتحقق من المستخدم المسجّل محلياً
        val session = SessionManager(this)
        val userId = session.getUserId()

        // تأخير بسيط فقط للشعار (اختياري)
        window.decorView.postDelayed({
            if (userId == -1) {
                // 🚪 مافي مستخدم → نروح لصفحة تسجيل الدخول
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                // 👤 فيه مستخدم → نجيب بياناته من Room
                userVm.getUserById(userId) { user ->
                    runOnUiThread {
                        if (user == null) {
                            // المستخدم انمسح أو الجلسة فاسدة
                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            if (user.healthStatus.isNullOrBlank() || user.healthStatus == "none") {
                                // أول مرة يسجل أو ما اختار حالته بعد
                                startActivity(Intent(this, HealthStatusActivity::class.java))
                            } else {
                                // حالته محفوظة → الصفحة الرئيسية
                                startActivity(Intent(this, SmartHomeActivity::class.java))
                            }
                        }
                        finish()
                    }
                }
            }
        }, 2000) // 2 ثانية للشعار فقط
    }
}
