package com.example.akillitarifuygulamasi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.entity.CommentWithUserAndRecipe
import com.example.akillitarifuygulamasi.databinding.ItemCommentAdminBinding

class CommentsAdminAdapter(
    private val list: List<CommentWithUserAndRecipe>,
    private val onDeleteClick: (CommentWithUserAndRecipe) -> Unit
) : RecyclerView.Adapter<CommentsAdminAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentAdminBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = list[position]

        holder.binding.txtUser.text = "Kullanıcı Hesabı: ${item.userName}"
        holder.binding.txtRecipe.text = "Tarif: ${item.recipeTitle}"
        holder.binding.txtComment.text = item.text

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }


    override fun getItemCount() = list.size
}
