package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.databinding.ActivityAdminManageRecipesBinding
import com.example.akillitarifuygulamasi.ui.RecipesAdminAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminManageRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminManageRecipesBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    private lateinit var adapter: RecipesAdminAdapter
    private var recipes = listOf<RecipeEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminManageRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerRecipes.layoutManager = LinearLayoutManager(this)

        adapter = RecipesAdminAdapter(
            recipes,
            onEditClick = { recipe ->
                val i = Intent(this, AdminEditRecipeActivity::class.java)
                i.putExtra("recipeId", recipe.id)
                startActivity(i)
            },
            onDeleteClick = { recipe ->
                deleteRecipe(recipe)
            }
        )

        binding.recyclerRecipes.adapter = adapter

        binding.btnAddRecipe.setOnClickListener {
            startActivity(Intent(this, AdminAddRecipeActivity::class.java))
        }

        loadRecipes()
    }

    override fun onResume() {
        super.onResume()
        loadRecipes()
    }

    private fun loadRecipes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = db.recipeDao().getAllNow()

            withContext(Dispatchers.Main) {
                recipes = list
                adapter.updateData(list)  // 🔥 تحديث حقيقي بدون recreate
            }
        }
    }

    private fun deleteRecipe(recipe: RecipeEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.ingredientDao().deleteByRecipe(recipe.id)
            db.recipeDao().delete(recipe)

            loadRecipes() // تحديث بعد الحذف
        }
    }
}
