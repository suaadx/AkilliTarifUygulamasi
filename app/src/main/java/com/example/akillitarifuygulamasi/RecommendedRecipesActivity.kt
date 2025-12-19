package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.akillitarifuygulamasi.data.ai.HealthTag
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.ui.RecipeAdapter
import com.example.akillitarifuygulamasi.ui.viewmodel.RatingViewModel
import com.example.akillitarifuygulamasi.ui.viewmodel.RecipeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.akillitarifuygulamasi.data.ai.HealthGate


class RecommendedRecipesActivity : BaseActivity() {

    private val recipeVm: RecipeViewModel by viewModels()
    private val ratingVm: RatingViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: RecipeAdapter

    // ✅ القائمة الكاملة فقط
    private var fullList: List<RecipeEntity> = emptyList()
    private val PAGE_SIZE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended_recipes)

        // ---------------- Session (مرة واحدة فقط وبالمكان الصحيح) ----------------
        val session = SessionManager(this)
        val userId = session.getUserId()
        val userHealth: HealthTag? = HealthTag.from(session.getHealthStatus())

        // Debug سريع عشان نتأكد
        Log.d("AI_DEBUG", "SESSION healthRaw=${session.getHealthStatus()} -> userHealth=$userHealth")

        // ---------------- Bottom Nav ----------------
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, SmartHomeActivity::class.java))
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavorilerActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    session.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // ---------------- UI ----------------
        recyclerView = findViewById(R.id.recipesRecyclerView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        // ---------------- Meal ----------------
        val mealList = intent.getStringArrayListExtra("MEAL_LIST") ?: emptyList()

        // ---------------- Adapter ----------------
        adapter = RecipeAdapter(
            mutableListOf(),
            this,
            ratingVm,
            { openDetail(it) },
            null
        )
        recyclerView.adapter = adapter

        // ---------------- Initial Load ----------------
        swipeRefresh.isRefreshing = true
        recipeVm.getSmartRecommendations(
            userId = userId,
            userHealth = userHealth,
            mealFilter = mealList
        ) { result ->
            runOnUiThread {

                val filtered = result.filter { recipe ->
                    val healthResult = HealthGate.check(
                        textRaw = recipe.title + " " + recipe.description,
                        userHealthTags = userHealth?.let { setOf(it) } ?: emptySet()
                    )

                    if (!healthResult.allowed) {
                        Log.d(
                            "AI_FILTER",
                            "REJECT ${recipe.title} | ${healthResult.reason}"
                        )
                    }

                    healthResult.allowed
                }

                fullList = filtered
                adapter.updateData(getRefreshedRecipes())
                swipeRefresh.isRefreshing = false
            }
        }


        // ---------------- Swipe Refresh (التحديث الحقيقي) ----------------
        swipeRefresh.setOnRefreshListener {
            adapter.updateData(getRefreshedRecipes())
            swipeRefresh.isRefreshing = false
        }
    }

    // 🔥 التحديث الذكي الحقيقي (خلط + أخذ عدد ثابت)
    private fun getRefreshedRecipes(): List<RecipeEntity> {
        if (fullList.isEmpty()) return emptyList()
        return fullList.shuffled().take(PAGE_SIZE)
    }

    private fun openDetail(recipe: RecipeEntity) {
        startActivity(
            Intent(this, RecipeDetailActivity::class.java)
                .putExtra("recipe_id", recipe.id)
        )
    }
}
