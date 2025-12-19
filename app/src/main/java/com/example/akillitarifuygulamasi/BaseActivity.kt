package com.example.akillitarifuygulamasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

open class BaseActivity : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        // نحاول نلقط التول بار من أي شاشة فيها Toolbar باسم mainToolbar
        val toolbar = findViewById<Toolbar?>(R.id.mainToolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }
}
