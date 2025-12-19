package com.example.akillitarifuygulamasi.ui

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.databinding.ItemRecipeAdminBinding
import java.io.File

class RecipesAdminAdapter(
    private var list: List<RecipeEntity>,
    private val onEditClick: (RecipeEntity) -> Unit,
    private val onDeleteClick: (RecipeEntity) -> Unit
) : RecyclerView.Adapter<RecipesAdminAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(val binding: ItemRecipeAdminBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = list[position]

        holder.binding.txtRecipeTitle.text = item.title

        // ------- تحميل الصورة لو عنده imagePath -------
        if (!item.imagePath.isNullOrEmpty()) {
            val file = File(item.imagePath!!)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.binding.imgRecipe.setImageBitmap(bitmap)
            } else {
                holder.binding.imgRecipe.setImageResource(android.R.color.darker_gray)
            }
        } else {
            holder.binding.imgRecipe.setImageResource(android.R.color.darker_gray)
        }
        // -------------------------------------------------

        holder.binding.btnEdit.setOnClickListener { onEditClick(item) }
        holder.binding.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = list.size

    // 🔥 تحديث القائمة بعد التعديل أو الإضافة
    fun updateData(newList: List<RecipeEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}
