package com.example.akillitarifuygulamasi

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.entity.CommentEntity
import com.example.akillitarifuygulamasi.ui.viewmodel.*
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.akillitarifuygulamasi.ui.viewmodel.RecipeViewModel


class RecipeDetailActivity : AppCompatActivity() {

    private val recipeVm: RecipeViewModel by viewModels()
    private val ratingVm: RatingViewModel by viewModels()
    private val favVm: FavoriteViewModel by viewModels()
    private val commentVm: CommentViewModel by viewModels()
    private val userVm: UserViewModel by viewModels()

    private var userId = 0
    private var recipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        recipeId = intent.getIntExtra("recipe_id", -1)
        userId = SessionManager(this).getUserId()

        // ============================
        // ربط الواجهة
        // ============================
        val imgRecipe = findViewById<ImageView>(R.id.imgDetailRecipe)
        val tvName = findViewById<TextView>(R.id.tvDetailRecipeName)
        val tvIngredients = findViewById<TextView>(R.id.tvIngredients)
        val tvInstructions = findViewById<TextView>(R.id.tvInstructions)
        val tvCalories = findViewById<TextView>(R.id.tvCalories)
        val tvAvg = findViewById<TextView>(R.id.tvAverageRating)
        val tvCount = findViewById<TextView>(R.id.tvRatingCount)
        val ratingBar = findViewById<RatingBar>(R.id.detailRatingBar)
        val btnFav = findViewById<ImageView>(R.id.btnFavorite)

        // ============================
        // ⭐ جلب الوصفة + المكونات (الحل الصحيح)
        // ============================
        recipeVm.getRecipeWithIngredients(recipeId)
            .observe(this) { data ->

                tvName.text = data.recipe.title
                tvInstructions.text = data.recipe.description
                tvCalories.text = "${data.recipe.calories} kcal"

                if (data.recipe.imageResId != null) {
                    imgRecipe.setImageResource(data.recipe.imageResId)
                } else {
                    imgRecipe.setImageResource(R.drawable.rounded_outline)
                }

                tvIngredients.text =
                    if (data.ingredients.isEmpty()) {
                        "Malzeme bilgisi yok"
                    } else {
                        data.ingredients.joinToString("\n") { "• ${it.name}" }
                    }
            }

        // ============================
        // ⭐ التقييم
        // ============================
        lifecycleScope.launch {
            ratingVm.getUserRating(userId, recipeId) {
                if (it != null) ratingBar.rating = it.toFloat()
            }
        }

        ratingVm.observeAverage(recipeId).observe(this) { avg ->
            tvAvg.text = "Ortalama: ${String.format("%.1f", avg ?: 0.0)} ⭐"
        }

        ratingVm.getCount(recipeId).observe(this) { count ->
            tvCount.text = "$count kişi oy verdi"
        }

        ratingBar.setOnRatingBarChangeListener { _, value, _ ->
            lifecycleScope.launch {
                ratingVm.rate(userId, recipeId, value.toInt())
                Toast.makeText(this@RecipeDetailActivity, "Teşekkürler!", Toast.LENGTH_SHORT).show()
            }
        }

        // ============================
        // ❤️ المفضلة
        // ============================
        favVm.isFavorite(recipeId, userId) { isFav ->
            btnFav.setImageResource(
                if (isFav) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }

        btnFav.setOnClickListener {
            favVm.toggleFavorite(recipeId, userId) { isFav ->
                btnFav.setImageResource(
                    if (isFav) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
            }
        }

        // ============================
        // 💬 التعليقات
        // ============================
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSend = findViewById<Button>(R.id.btnSendComment)
        val recyclerComments = findViewById<RecyclerView>(R.id.recyclerComments)

        val commentList = mutableListOf<CommentEntity>()

        val adapter = CommentAdapter(
            list = commentList,
            currentUserId = userId,
            getUserName = { id, callback ->
                userVm.getUserName(id) { callback(it) }
            },
            onDelete = { comment ->
                commentVm.deleteComment(comment)
            }
        )

        recyclerComments.layoutManager = LinearLayoutManager(this)
        recyclerComments.adapter = adapter

        commentVm.getComments(recipeId).observe(this) { list ->
            commentList.clear()
            commentList.addAll(list)
            adapter.notifyDataSetChanged()
        }

        btnSend.setOnClickListener {
            val text = etComment.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            commentVm.addComment(recipeId, userId, text)
            etComment.setText("")
        }
    }
}
