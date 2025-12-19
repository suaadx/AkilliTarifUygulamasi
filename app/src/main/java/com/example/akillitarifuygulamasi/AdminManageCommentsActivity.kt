package com.example.akillitarifuygulamasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.databinding.ActivityManageCommentsBinding
import com.example.akillitarifuygulamasi.ui.CommentsAdminAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminManageCommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageCommentsBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerComments.layoutManager = LinearLayoutManager(this)

        loadComments()
    }

    private fun loadComments() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = db.commentDao().getAllCommentsAdmin()

            withContext(Dispatchers.Main) {
                binding.recyclerComments.adapter =
                    CommentsAdminAdapter(list) { comment ->
                        deleteComment(comment.id)
                    }
            }
        }
    }

    private fun deleteComment(commentId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.commentDao().deleteById(commentId)

            // ينسجل في activity log
            db.userActivityDao().insert(
                com.example.akillitarifuygulamasi.data.entity.UserActivityEntity(
                    userId = 0, // أدمن
                    recipeId = null,
                    actionType = "DELETE_COMMENT",
                    details = "Yorum admin tarafından silindi",
                    createdAt = System.currentTimeMillis()
                )
            )

            loadComments()
        }
    }
}
