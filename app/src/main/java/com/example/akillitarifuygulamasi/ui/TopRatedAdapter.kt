package com.example.akillitarifuygulamasi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.model.TopRatedRecipeItem
import com.example.akillitarifuygulamasi.databinding.ItemTopRatedBinding

class TopRatedAdapter(
    private val list: List<TopRatedRecipeItem>,
    private val onOpen: (Int) -> Unit
) : RecyclerView.Adapter<TopRatedAdapter.Holder>() {

    inner class Holder(val binding: ItemTopRatedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemTopRatedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]

        holder.binding.txtTitle.text = item.title
        holder.binding.txtRating.text = "⭐ ${String.format("%.1f", item.avgRating)}   (${item.ratingCount} oy)"

        holder.itemView.setOnClickListener {
            onOpen(item.recipeId)
        }
    }

    override fun getItemCount() = list.size
}
