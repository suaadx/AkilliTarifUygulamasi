package com.example.akillitarifuygulamasi.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.data.entity.UserEntity
import com.example.akillitarifuygulamasi.databinding.ItemUserBinding
import com.example.akillitarifuygulamasi.R

class UsersAdapter(
    private val users: List<UserEntity>,
    private val onDeleteClick: (UserEntity) -> Unit,
    private val onRoleChangeClick: (UserEntity) -> Unit,
    private val onStatusChangeClick: (UserEntity) -> Unit,
    private val onUserActivityClick: (UserEntity) -> Unit   // ← جديد
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.binding.tvName.text = user.name
        holder.binding.tvEmail.text = user.email
        holder.binding.tvHealth.text = user.healthStatus
        holder.binding.tvRole.text = "Rol: ${user.role}"

        // ----------------------------------------------------------
        // 🔒 زر إيقاف/تفعيل الحساب + تغيير اللون + تغيير الأيقونة
        // ----------------------------------------------------------
        holder.binding.btnToggleStatus.apply {

            val isActive = user.status == "active"

            text = if (isActive) "Hesabı Durdur" else "Hesabı Aktifleştir"

            backgroundTintList = ColorStateList.valueOf(
                if (isActive) Color.parseColor("#9C27B0") else Color.parseColor("#9E9E9E")
            )

            // 🔒 قفل مغلق = active
            // 🔓 قفل مفتوح = suspended
            setCompoundDrawablesWithIntrinsicBounds(
                if (isActive) R.drawable.ic_unlock else android.R.drawable.ic_lock_idle_lock,
                0, 0, 0
            )

            setOnClickListener { onStatusChangeClick(user) }
        }

        // زر تغيير الدور
        holder.binding.btnChangeRole.setOnClickListener {
            onRoleChangeClick(user)
        }

        // زر الحذف
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(user)
        }

        // ----------------------------------------------------------
        // 🔵 زر سجل نشاط المستخدم
        // ----------------------------------------------------------
        holder.binding.btnUserActivity.setOnClickListener {
            onUserActivityClick(user)
        }
    }

    override fun getItemCount() = users.size
}
