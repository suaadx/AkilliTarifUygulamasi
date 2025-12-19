package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

abstract class BaseDrawerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var toolbarTitle: TextView

    /** كل صفحة ترجع Layout المحتوى الخاص بها */
    @LayoutRes
    protected abstract fun getChildLayoutResId(): Int

    /** نص العنوان في الشريط العلوي (اختياري) */
    protected open fun getToolbarTitle(): String = "Akıllı Tarif"

    /** عشان ما نعيد فتح نفس الصفحة من القائمة */
    protected open fun selfMenuId(): Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_drawer)

        drawerLayout   = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        btnMenu        = findViewById(R.id.btnMenu)
        toolbarTitle   = findViewById(R.id.toolbarTitle)

        toolbarTitle.text = getToolbarTitle()

        // حقن المحتوى الخاص بكل صفحة داخل الحاوية
        val container = findViewById<FrameLayout>(R.id.contentContainer)
        LayoutInflater.from(this).inflate(getChildLayoutResId(), container, true)

        // افتح/اغلق الدرج (تأكد أن navigationView له layout_gravity="end" في XML)
        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.END) }

        navigationView.setNavigationItemSelectedListener { item ->
            val currentId = selfMenuId()
            if (currentId != null && item.itemId == currentId) {
                drawerLayout.closeDrawer(GravityCompat.END)
                return@setNavigationItemSelectedListener true
            }

            when (item.itemId) {
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.nav_favorites -> startActivity(Intent(this, FavorilerActivity::class.java))
                R.id.nav_logout -> {
                    // امسح معلومات تسجيل الدخول إن وجدت
                    getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()
                    Toast.makeText(this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                }
                else -> { /* عناصر أخرى إن أضفتها لاحقاً */ }
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (this::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
