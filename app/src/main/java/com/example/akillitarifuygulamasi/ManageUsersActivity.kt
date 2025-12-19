package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.data.AppDatabase
import com.example.akillitarifuygulamasi.data.entity.UserEntity
import com.example.akillitarifuygulamasi.databinding.ActivityManageUsersBinding
import com.example.akillitarifuygulamasi.ui.UsersAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageUsersBinding
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerUsers.layoutManager = LinearLayoutManager(this)

        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val users = db.userDao().getAllUsers()

            withContext(Dispatchers.Main) {
                binding.recyclerUsers.adapter = UsersAdapter(
                    users = users,
                    onDeleteClick       = { user -> deleteUser(user) },
                    onRoleChangeClick   = { user -> toggleRole(user) },
                    onStatusChangeClick = { user -> toggleStatus(user) },
                    onUserActivityClick = { user ->
                        // 🔵 فتح صفحة سجل نشاط هذا المستخدم
                        val intent = Intent(this@ManageUsersActivity, UserActivityLogActivity::class.java)
                        intent.putExtra("userId", user.id)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun deleteUser(user: UserEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.userDao().deleteUser(user.id)
            loadUsers()
        }
    }

    private fun toggleRole(user: UserEntity) {
        val newRole = if (user.role == "user") "admin" else "user"

        lifecycleScope.launch(Dispatchers.IO) {
            db.userDao().updateRole(user.id, newRole)
            loadUsers()
        }
    }

    private fun toggleStatus(user: UserEntity) {
        val newStatus = if (user.status == "active") "suspended" else "active"

        lifecycleScope.launch(Dispatchers.IO) {
            db.userDao().updateStatus(user.id, newStatus)
            loadUsers()
        }
    }
}
