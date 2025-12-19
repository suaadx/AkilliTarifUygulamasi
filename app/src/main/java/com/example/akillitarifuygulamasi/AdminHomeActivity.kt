package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.databinding.ActivityAdminHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // تحميل بيانات لوحة التحكم
        loadDashboardData()

        // إدارة الوصفات
        binding.btnManageRecipes.setOnClickListener {
            startActivity(Intent(this, AdminManageRecipesActivity::class.java))
        }

        // إدارة التعليقات
        binding.btnManageComments.setOnClickListener {
            startActivity(Intent(this, AdminManageCommentsActivity::class.java))
        }

        // أعلى الوصفات تقييمًا
        binding.btnTopRated.setOnClickListener {
            startActivity(Intent(this, TopRatedRecipesActivity::class.java))
        }

        // إدارة المستخدمين
        binding.btnManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }

        // إدارة الحالات الصحية الجديدة
        val btnManageHealth = findViewById<Button>(R.id.btnManageHealth)
        btnManageHealth.setOnClickListener {
            startActivity(Intent(this, AdminManageHealthActivity::class.java))
        }
    }


    // =============================
    //      loadDashboardData()
    // =============================
    private fun loadDashboardData() {
        lifecycleScope.launch(Dispatchers.IO) {

            val totalUsers = db.userDao().countUsers()
            val totalRecipes = db.recipeDao().count()

            withContext(Dispatchers.Main) {
                binding.tvTotalUsers.text = "Toplam kullanıcı: $totalUsers"
                binding.tvTotalRecipes.text = "Toplam tarif: $totalRecipes"
            }
        }

        // مراقبة عدد التعليقات
        db.commentDao().getTotalComments().observe(this) { count ->
            binding.tvTotalComments.text = "Toplam yorum: $count"
        }
    }
}
