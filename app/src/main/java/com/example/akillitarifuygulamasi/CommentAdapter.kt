package com.example.akillitarifuygulamasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.entity.CommentEntity

class CommentAdapter(
    private val list: List<CommentEntity>,
    private val currentUserId: Int,

    // 👈 الدالة الجديدة: ترجع الاسم عبر callback وليس بشكل مباشر
    private val getUserName: (Int, (String) -> Unit) -> Unit,

    private val onDelete: (CommentEntity) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvComment: TextView = itemView.findViewById(R.id.tvComment)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val c = list[position]

        // ⭐ هنا نجيب الاسم من Room بطريقة صحيحة
        getUserName(c.userId) { name ->
            holder.tvUserName.text = name
        }

        holder.tvComment.text = c.text

        holder.tvDate.text = android.text.format.DateFormat.format(
            "dd MMM yyyy - HH:mm",
            c.createdAt
        )

        if (c.userId == currentUserId) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener { onDelete(c) }
        } else {
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = list.size
}
