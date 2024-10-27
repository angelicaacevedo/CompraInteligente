package br.com.angelica.comprainteligente

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.angelica.comprainteligente.presentation.navigation.AppNavigation
import br.com.angelica.comprainteligente.theme.CompraInteligenteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recupera o userId do SharedPreferences
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null) ?: ""

        setContent {
            CompraInteligenteTheme {
                AppNavigation(userId)
            }
        }
    }
}

