package com.example.akillitarifuygulamasi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.ai.TextNormalizer
import com.example.akillitarifuygulamasi.data.entity.IngredientEntity
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.databinding.ActivityAdminAddRecipeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AdminAddRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddRecipeBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    private var selectedImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMealDropdown()

        binding.btnSelectImage.setOnClickListener {
            pickImage()
        }

        binding.btnSave.setOnClickListener {
            saveRecipe()
        }
    }

    // --------------------------------------------------
    // Image
    // --------------------------------------------------
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2001 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            binding.imgPreview.setImageURI(uri)
            selectedImagePath = copyImageToInternal(uri)
        }
    }

    private fun copyImageToInternal(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = getFileName(uri)
            val file = File(filesDir, fileName)

            val output = FileOutputStream(file)
            inputStream?.copyTo(output)

            output.close()
            inputStream?.close()

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "image_${System.currentTimeMillis()}.jpg"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
            cursor.close()
        }
        return name
    }

    // --------------------------------------------------
    // Dropdown
    // --------------------------------------------------
    private fun setupMealDropdown() {
        val meals = listOf("kahvalti", "ogle", "aksam", "tatli", "aperatif")
        binding.edtMeal.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, meals)
        )
        binding.edtMeal.setOnClickListener { binding.edtMeal.showDropDown() }
    }

    // --------------------------------------------------
    // Save Recipe (CLEAN)
    // --------------------------------------------------
    private fun saveRecipe() {
        val title = binding.edtTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Tarif adı boş olamaz", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.edtDescription.text.toString().trim()
        val meal = binding.edtMeal.text.toString().trim()
        val ingredientsRaw = binding.edtIngredients.text.toString()

        val ingredientsList = ingredientsRaw
            .split(",", "\n")
            .map { TextNormalizer.normalize(it) }
            .filter { it.isNotBlank() }

        if (ingredientsList.isEmpty()) {
            Toast.makeText(this, "En az bir malzeme girmelisiniz", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {

            val recipeId = db.recipeDao().insert(
                RecipeEntity(
                    title = title,
                    description = description,
                    calories = 0,
                    meal = meal,
                    healthTag = "none", // ⭐ النظام يستنتج لاحقًا
                    imageResId = null,
                    imagePath = selectedImagePath
                )
            )

            ingredientsList.forEach { ing ->
                db.ingredientDao().insertOne(
                    IngredientEntity(
                        recipeId = recipeId.toInt(),
                        name = ing,
                        quantity = ""
                    )
                )
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@AdminAddRecipeActivity,
                    "Tarif başarıyla kaydedildi",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
