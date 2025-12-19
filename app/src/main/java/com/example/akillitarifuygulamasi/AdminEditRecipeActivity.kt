package com.example.akillitarifuygulamasi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.IngredientEntity
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.databinding.ActivityAdminEditRecipeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminEditRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminEditRecipeBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    private var recipeId = -1
    private var selectedImagePath: String? = null

    private val mealOptions = listOf("Kahvaltı", "Öğle Yemeği", "Akşam Yemeği", "Ara Öğün")

    // لفتح معرض الصور
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    binding.imgPreview.setImageURI(uri)
                    selectedImagePath = uri.toString()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeId = intent.getIntExtra("recipeId", -1)
        if (recipeId == -1) {
            Toast.makeText(this, "Geçersiz tarif", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupMealDropdown()
        loadHealthTags()
        loadRecipe()

        binding.btnSelectImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }

    // فتح المعرض
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun setupMealDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mealOptions)
        binding.edtMeal.setAdapter(adapter)
    }

    // تحميل الوسوم الصحية من Room
    private fun loadHealthTags() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = db.healthStatusDao().getAll().map { it.name }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@AdminEditRecipeActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    list
                )
            }
        }
    }

    // تحميل بيانات الوصفة القديمة
    private fun loadRecipe() {
        lifecycleScope.launch(Dispatchers.IO) {
            val recipe = db.recipeDao().getById(recipeId)
            val ingredients = db.ingredientDao().getByRecipeNow(recipeId)

            if (recipe == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminEditRecipeActivity, "Tarif bulunamadı", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return@launch
            }

            val ingText = ingredients.joinToString(", ") { it.name }

            withContext(Dispatchers.Main) {
                binding.edtTitle.setText(recipe.title)
                binding.edtDescription.setText(recipe.description ?: "")
                binding.edtMeal.setText(recipe.meal ?: "", false)
                binding.edtIngredients.setText(ingText)

                selectedImagePath = recipe.imagePath

                if (recipe.imagePath != null) {
                    try { binding.imgPreview.setImageURI(Uri.parse(recipe.imagePath)) }
                    catch (_: Exception) {}
                }
            }
        }
    }

    // حفظ التعديلات
    private fun saveChanges() {
        val title = binding.edtTitle.text.toString().trim()
        val description = binding.edtDescription.text.toString().trim()
        val meal = binding.edtMeal.text.toString().trim()
        val ingredientsRaw = binding.edtIngredients.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Tarif adı boş olamaz", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {

            val old = db.recipeDao().getById(recipeId)
            if (old == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminEditRecipeActivity, "Tarif bulunamadı", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val updated = old.copy(
                title = title,
                description = description,
                meal = meal,
                imagePath = selectedImagePath
            )

            db.recipeDao().update(updated)

            // تحديث المكونات
            db.ingredientDao().deleteByRecipe(recipeId)

            val ingredientsList = ingredientsRaw.split(",").map { it.trim() }
            ingredientsList.forEach { ing ->
                if (ing.isNotEmpty()) {
                    db.ingredientDao().insertOne(
                        IngredientEntity(
                            recipeId = recipeId,
                            name = ing,
                            quantity = ""
                        )
                    )
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AdminEditRecipeActivity, "Tarif güncellendi", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
