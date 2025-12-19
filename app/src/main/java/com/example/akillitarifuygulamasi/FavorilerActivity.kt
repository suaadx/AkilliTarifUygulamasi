package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.ui.viewmodel.FavoriteViewModel
import com.example.akillitarifuygulamasi.ui.viewmodel.RatingViewModel
import com.example.akillitarifuygulamasi.ui.viewmodel.RecipeViewModel
import androidx.appcompat.widget.Toolbar
import com.example.akillitarifuygulamasi.ui.RecipeAdapter

class FavorilerActivity : BaseActivity() {

    private val ratingVm: RatingViewModel by viewModels()
    private val favVm: FavoriteViewModel by viewModels()
    private val recipeVm: RecipeViewModel by viewModels()

    private val favoriteRecipes = mutableListOf<RecipeEntity>()

    // 🔥 هنا التعريف حتى يكون مرئي في كل مكان
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoriler)

        val bottomNav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.nav_favorites

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, SmartHomeActivity::class.java))
                    true
                }

                R.id.nav_favorites -> true // نفس الصفحة

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.nav_logout -> {
                    SessionManager(this).logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_favorites)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = SessionManager(this).getUserId()

        // ⭐ الآن نستخدم adapter المتعرفة فوق، وليس inside
        adapter = RecipeAdapter(
            recipes = favoriteRecipes,
            lifecycleOwner = this,
            ratingVm = ratingVm,
            onDetailClick = { recipe ->
                val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                    putExtra("recipe_id", recipe.id)
                    putExtra("recipe_name", recipe.title)
                    putExtra("recipe_image", recipe.imageResId)
                    putExtra("recipe_calories", recipe.calories)
                    putExtra("recipe_instructions", recipe.description)
                }
                startActivity(intent)
            },
            onRemoveFavorite = { recipe ->
                val recipeId = recipe.id

                // (1) حذف من قاعدة البيانات
                favVm.removeFavorite(userId, recipeId)

                // (2) حذف من القائمة
                val index = favoriteRecipes.indexOfFirst { it.id == recipeId }
                if (index != -1) {
                    favoriteRecipes.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }
        )

        recyclerView.adapter = adapter

        // تحميل المفضلة
        favVm.getFavorites(userId).observe(this) { favList ->
            val ids = favList.map { it.recipeId }

            recipeVm.getByIds(ids).observe(this) { recipes ->
                favoriteRecipes.clear()
                favoriteRecipes.addAll(recipes)
                adapter.notifyDataSetChanged()
            }
        }
    }

}
