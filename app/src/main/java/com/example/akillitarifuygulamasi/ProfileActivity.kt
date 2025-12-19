package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.akillitarifuygulamasi.ui.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : BaseActivity() {

    private val userVm: UserViewModel by viewModels()
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // ---------- Bottom Navigation ----------
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, SmartHomeActivity::class.java))
                    true
                }

                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavorilerActivity::class.java))
                    true
                }

                R.id.nav_profile -> true

                R.id.nav_logout -> {
                    SessionManager(this).logout()
                    startActivity(
                        Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                    true
                }

                else -> false
            }
        }

        // ---------- Toolbar ----------
        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // ---------- Views ----------
        val userNameText   = findViewById<TextView>(R.id.userNameText)
        val userHealthText = findViewById<TextView>(R.id.userHealthText)
        val editProfileBtn = findViewById<Button>(R.id.editProfileButton)
        val editHealthBtn  = findViewById<Button>(R.id.editHealthButton)
        val logoutButton   = findViewById<Button>(R.id.logoutButton)

        swipeRefresh = findViewById(R.id.swipeRefresh)

        // ---------- Load profile ----------
        swipeRefresh.isRefreshing = true
        loadProfileData(userNameText, userHealthText)

        swipeRefresh.setOnRefreshListener {
            loadProfileData(userNameText, userHealthText)
        }

        // ---------- Buttons ----------
        val session = SessionManager(this)

        editProfileBtn.setOnClickListener {
            val i = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("EXTRA_ID", session.getUserId())
            }
            startActivity(i)
        }

        editHealthBtn.setOnClickListener {
            startActivity(
                Intent(this, HealthStatusActivity::class.java)
                    .putExtra("EDIT_MODE", true)
                    .putExtra("user_id", session.getUserId())
            )
        }

        logoutButton.setOnClickListener {
            session.logout()
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }

    // ---------- Refresh Logic ----------
    private fun loadProfileData(
        userNameText: TextView,
        userHealthText: TextView
    ) {
        val session = SessionManager(this)
        val userId = session.getUserId()

        if (userId != -1) {
            userVm.getUserById(userId) { user ->
                runOnUiThread {
                    if (user != null) {
                        userNameText.text = "İsim: ${user.name}"
                        userHealthText.text = "Sağlık durumu: ${user.healthStatus}"
                    }
                    swipeRefresh.isRefreshing = false
                }
            }
        } else {
            swipeRefresh.isRefreshing = false
        }
    }
}
