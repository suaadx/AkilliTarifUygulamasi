package com.example.akillitarifuygulamasi

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // ------------------------------------------------------
    // حفظ بيانات المستخدم
    // ------------------------------------------------------
    fun saveUser(id: Int, email: String, name: String) {
        prefs.edit()
            .putInt("user_id", id)
            .putString("user_email", email)
            .putString("user_name", name)
            .putBoolean("is_logged_in", true)
            .apply()
    }


    fun saveHealthStatus(healthStatus: String?) {
        prefs.edit()
            .putString("user_health_status", healthStatus)
            .apply()
    }

    // ------------------------------------------------------
    // استرجاع البيانات
    // ------------------------------------------------------
    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun getUserName(): String? = prefs.getString("user_name", null)

    fun setHealthStatus(status: String) {
        prefs.edit()
            .putString("user_health_status", status)
            .apply()
    }

    fun getHealthStatus(): String? =
        prefs.getString("user_health_status", null)


    fun isLoggedIn(): Boolean =
        prefs.getBoolean("is_logged_in", false)

    fun clearHealthStatus() {
        prefs.edit()
            .remove("user_health_status")
            .apply()
    }

    // ------------------------------------------------------
    // ✅ تسجيل خروج صحيح (لا يمسح هوية المستخدم)
    // ------------------------------------------------------
    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .apply()
    }

    // ------------------------------------------------------
    // ❗ مسح كامل (استخدمه فقط لو أردت Reset للتطبيق)
    // ------------------------------------------------------
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
