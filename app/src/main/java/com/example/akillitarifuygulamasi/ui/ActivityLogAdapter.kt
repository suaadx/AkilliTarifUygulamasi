package com.example.akillitarifuygulamasi.ui
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.akillitarifuygulamasi.data.entity.UserActivityEntity
import com.example.akillitarifuygulamasi.databinding.ItemActivityLogBinding
import java.text.SimpleDateFormat
import java.util.*

class ActivityLogAdapter :
    ListAdapter<UserActivityEntity, ActivityLogAdapter.ActivityViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<UserActivityEntity>() {
        override fun areItemsTheSame(old: UserActivityEntity, new: UserActivityEntity) =
            old.id == new.id

        override fun areContentsTheSame(old: UserActivityEntity, new: UserActivityEntity) =
            old == new
    }

    class ActivityViewHolder(val binding: ItemActivityLogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding =
            ItemActivityLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val item = getItem(position)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(item.createdAt))

        holder.binding.txtAction.text = when (item.actionType) {
            "LIKE" -> "قام بإعجاب على وصفة #${item.recipeId}"
            "UNLIKE" -> "أزال الإعجاب عن وصفة #${item.recipeId}"
            "COMMENT" -> "كتب تعليق: ${item.details}"
            "DELETE_COMMENT" -> "حذف تعليق: ${item.details}"
            "RATING" -> "قيّم الوصفة: ${item.details}"
            else -> item.actionType
        }

        holder.binding.txtDate.text = formattedDate
    }
}
