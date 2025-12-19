package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.model.RatingSummary
import com.example.akillitarifuygulamasi.data.model.TopRatedRecipeItem
import com.example.akillitarifuygulamasi.databinding.ActivityTopRatedRecipesBinding
import com.example.akillitarifuygulamasi.ui.TopRatedAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopRatedRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopRatedRecipesBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopRatedRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerTopRated.layoutManager = LinearLayoutManager(this)

        loadTopRated()
    }

    private fun loadTopRated() {
        lifecycleScope.launch(Dispatchers.IO) {

            // 1) نجيب ملخص التقييمات
            val ratingSummaryList = db.ratingDao().getAllRatingsSummary()

            // 2) نجيب الوصفات بناءً على الترتيب
            val finalList = mutableListOf<TopRatedRecipeItem>()

            ratingSummaryList.forEach { summary: RatingSummary ->
                val recipe = db.recipeDao().getById(summary.recipeId)
                if (recipe != null) {
                    finalList.add(
                        TopRatedRecipeItem(
                            recipeId = recipe.id,
                            title = recipe.title,
                            avgRating = summary.avgRating,
                            ratingCount = summary.ratingCount
                        )
                    )
                }
            }

            withContext(Dispatchers.Main) {
                binding.recyclerTopRated.adapter = TopRatedAdapter(
                    finalList,
                    onOpen = { recipeId ->
                        val i = Intent(this@TopRatedRecipesActivity, AdminEditRecipeActivity::class.java)
                        i.putExtra("recipeId", recipeId)
                        startActivity(i)
                    }
                )
            }
        }
    }
}
