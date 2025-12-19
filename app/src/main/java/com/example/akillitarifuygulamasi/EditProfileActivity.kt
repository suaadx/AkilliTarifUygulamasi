package com.example.akillitarifuygulamasi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.akillitarifuygulamasi.data.entity.UserEntity
import com.example.akillitarifuygulamasi.ui.viewmodel.UserViewModel

class EditProfileActivity : AppCompatActivity() {

    private val userVm: UserViewModel by viewModels()
    private var currentUser: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val nameField = findViewById<EditText>(R.id.editName)
        val emailField = findViewById<EditText>(R.id.editEmail)
        val passField = findViewById<EditText>(R.id.editPassword)
        val confirmField = findViewById<EditText>(R.id.editConfirmPassword)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val skipText = findViewById<TextView>(R.id.skipPasswordText)

        val session = SessionManager(this)

        val userIdFromIntent = intent.getIntExtra("EXTRA_ID", -1)
        val userId = if (userIdFromIntent != -1) userIdFromIntent else session.getUserId()

        // 🔹 تحميل بيانات المستخدم
        if (userId != -1) {
            userVm.getUserById(userId) { user ->
                runOnUiThread {
                    if (user != null) {
                        currentUser = user
                        nameField.setText(user.name)
                        emailField.setText(user.email)
                    }
                }
            }
        }


        // 🔹 زر حفظ التعديلات
        updateButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val pass = passField.text.toString().trim()
            val confirm = confirmField.text.toString().trim()

            // تحقق أساسي
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // تحقق كلمة السر
            if (pass.isNotEmpty() && pass != confirm) {
                Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = currentUser ?: return@setOnClickListener

            val updatedUser = user.copy(
                name = name,
                email = email,
                password = if (pass.isNotEmpty()) pass else user.password
            )

            // 🔹 تحديث المستخدم في Room
            userVm.updateUser(updatedUser) {
                runOnUiThread {

                    // تحديث بيانات الجلسة لو تغيّر الإيميل
                    if (email != user.email) {
                        session.saveUser(
                            updatedUser.id,
                            updatedUser.email,
                            updatedUser.name
                        )
                    }


                    Toast.makeText(this, "Bilgiler güncellendi ✅", Toast.LENGTH_SHORT).show()
                    finish() // ترجع للصفحة السابقة (ProfileActivity)
                }
            }
        }

        // 🔹 تخطي تغيير كلمة السر
        skipText.setOnClickListener {
            passField.setText("")
            confirmField.setText("")
            Toast.makeText(this, "Şifre değiştirilmedi", Toast.LENGTH_SHORT).show()
        }
    }
}
