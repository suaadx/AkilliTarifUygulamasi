package com.example.akillitarifuygulamasi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akillitarifuygulamasi.ui.theme.AkilliTarifUygulamasiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // نجيب القيمة المرسلة من HealthStatusActivity
        val healthList = intent.getStringArrayListExtra("HEALTH_LIST") ?: arrayListOf()

        setContent {
            AkilliTarifUygulamasiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HealthStatusScreen(
                        healthList = healthList,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HealthStatusScreen(healthList: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Seçilen sağlık durumları:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        healthList.forEach {
            Text(text = "• $it", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

