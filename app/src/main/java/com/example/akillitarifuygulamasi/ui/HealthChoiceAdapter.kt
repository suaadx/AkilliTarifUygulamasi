package com.example.akillitarifuygulamasi.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.R
import com.example.akillitarifuygulamasi.data.entity.HealthStatusEntity

class HealthChoiceAdapter(
    private var items: List<HealthStatusEntity>,
    private val onSelected: (String) -> Unit
) : RecyclerView.Adapter<HealthChoiceAdapter.Holder>() {

    private var selectedIndex = -1

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkHealth)
        val textView: TextView = view.findViewById(R.id.tvHealthChoice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_choice, parent, false)
        return Holder(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]

        holder.textView.text = item.name

        // نلغي أي لسنر قديم قبل ما نغيّر isChecked
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = (position == selectedIndex)

        // كلك على السطر كله
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            selectedIndex = pos
            notifyDataSetChanged()
            onSelected(items[pos].name)
        }

        // كلك على الـ CheckBox نفسه
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val pos = holder.adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnCheckedChangeListener

                selectedIndex = pos
                notifyDataSetChanged()
                onSelected(items[pos].name)
            }
        }
    }

    fun updateData(newList: List<HealthStatusEntity>) {
        items = newList
        selectedIndex = -1
        notifyDataSetChanged()
    }
}
