package com.example.akillitarifuygulamasi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.akillitarifuygulamasi.ActivityDisplay
import com.example.akillitarifuygulamasi.databinding.ItemUserActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class UserActivityAdapter :
    ListAdapter<ActivityDisplay, UserActivityAdapter.ActivityViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<ActivityDisplay>() {
        override fun areItemsTheSame(old: ActivityDisplay, new: ActivityDisplay) =
            old.date == new.date && old.action == new.action

        override fun areContentsTheSame(old: ActivityDisplay, new: ActivityDisplay) =
            old == new
    }

    inner class ActivityViewHolder(val binding: ItemUserActivityBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemUserActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val item = getItem(position)

        // عنوان النشاط
        holder.binding.txtAction.text = item.action

        // نص التفاصيل مثل التعليق أو رقم التقييم
        holder.binding.txtDetails.text = item.details ?: ""

        // اسم الوصفة
        holder.binding.txtRecipe.text = "Tarif: ${item.recipeTitle}"

        // التاريخ
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("tr"))
        holder.binding.txtDate.text = sdf.format(Date(item.date))

        // أيقونة ولون الخلفية حسب نوع النشاط
        when (item.type) {
            "COMMENT" -> {
                holder.binding.txtIcon.text = "💬"
                holder.binding.root.setCardBackgroundColor(0xFFE3F2FD.toInt())
            }
            "DELETE_COMMENT" -> {
                holder.binding.txtIcon.text = "🗑️"
                holder.binding.root.setCardBackgroundColor(0xFFFFEBEE.toInt())
            }
            "LIKE" -> {
                holder.binding.txtIcon.text = "❤️"
                holder.binding.root.setCardBackgroundColor(0xFFFCE4EC.toInt())
            }
            "UNLIKE" -> {
                holder.binding.txtIcon.text = "💔"
                holder.binding.root.setCardBackgroundColor(0xFFF8BBD0.toInt())
            }
            "RATING" -> {
                holder.binding.txtIcon.text = "⭐"
                holder.binding.root.setCardBackgroundColor(0xFFFFF3CD.toInt())
            }
        }
    }
}
