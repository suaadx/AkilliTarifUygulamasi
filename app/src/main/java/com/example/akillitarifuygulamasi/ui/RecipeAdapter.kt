package com.example.akillitarifuygulamasi.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.R
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.ui.viewmodel.RatingViewModel

class RecipeAdapter(
    private var recipes: MutableList<RecipeEntity>,   // ← صارت Mutable عشان نقدر نحدثها
    private val lifecycleOwner: LifecycleOwner,
    private val ratingVm: RatingViewModel,
    private val onDetailClick: (RecipeEntity) -> Unit,
    private val onRemoveFavorite: ((RecipeEntity) -> Unit)? = null
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeName: TextView = itemView.findViewById(R.id.tvRecipeName)
        val recipeImage: ImageView = itemView.findViewById(R.id.imgRecipe)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val btnDetails: Button = itemView.findViewById(R.id.btnDetails)
        val btnRemoveFavorite: ImageView = itemView.findViewById(R.id.btnRemoveFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.recipeName.text = recipe.title

        // الصورة (إذا موجودة)
        recipe.imageResId?.let {
            holder.recipeImage.setImageResource(it)
        }

        // ⭐ التقييم
        ratingVm.observeAverage(recipe.id).observe(lifecycleOwner) { avg ->
            holder.ratingBar.rating = (avg ?: 0.0).toFloat()
        }

        // 🔹 التفاصيل
        holder.btnDetails.setOnClickListener {
            onDetailClick(recipe)
        }

        // ❤️ زر الحذف للمفضلة فقط
        if (onRemoveFavorite != null) {
            holder.btnRemoveFavorite.visibility = View.VISIBLE
            holder.btnRemoveFavorite.setOnClickListener {
                onRemoveFavorite.invoke(recipe)
            }
        } else {
            holder.btnRemoveFavorite.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = recipes.size

    // -------------------------------------------------------
    // 🔥 دالة تحديث البيانات — تستخدم في SmartHome و Search
    // -------------------------------------------------------
    fun updateData(newList: List<RecipeEntity>) {
        recipes.clear()
        recipes.addAll(newList)
        notifyDataSetChanged()
    }
}
