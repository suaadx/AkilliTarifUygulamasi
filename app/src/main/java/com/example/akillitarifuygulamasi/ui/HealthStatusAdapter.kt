package com.example.akillitarifuygulamasi.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.R
import com.example.akillitarifuygulamasi.data.entity.HealthStatusEntity

class HealthStatusAdapter(
    private val onDelete: (HealthStatusEntity) -> Unit
) : ListAdapter<HealthStatusEntity, HealthStatusAdapter.Holder>(Diff) {

    object Diff : DiffUtil.ItemCallback<HealthStatusEntity>() {
        override fun areItemsTheSame(oldItem: HealthStatusEntity, newItem: HealthStatusEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: HealthStatusEntity, newItem: HealthStatusEntity) =
            oldItem == newItem
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtName)
        val deleteBtn: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_status, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.name.text = item.name
        holder.deleteBtn.setOnClickListener { onDelete(item) }
    }
}
