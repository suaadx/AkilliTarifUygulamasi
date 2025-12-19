package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.databinding.ActivityTopFavoritedRecipesBinding
import com.example.akillitarifuygulamasi.ui.RecipesAdminAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopFavoritedRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopFavoritedRecipesBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopFavoritedRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerTopFavorited.layoutManager = LinearLayoutManager(this)

        loadTopFavorited()
    }

    private fun loadTopFavorited() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = db.recipeDao().getMostFavorited(20)

            withContext(Dispatchers.Main) {
                binding.recyclerTopFavorited.adapter = RecipesAdminAdapter(
                    list,
                    onEditClick = { recipe ->
                        val i = Intent(this@TopFavoritedRecipesActivity, AdminEditRecipeActivity::class.java)
                        i.putExtra("recipeId", recipe.id)
                        startActivity(i)
                    },
                    onDeleteClick = { /* اختياري */ }
                )
            }
        }
    }
}
