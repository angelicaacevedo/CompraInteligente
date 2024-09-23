package br.com.angelica.comprainteligente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import br.com.angelica.comprainteligente.presentation.navigation.NavGraph
import br.com.angelica.comprainteligente.theme.CompraInteligenteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompraInteligenteTheme {
                NavGraph()
            }
        }
    }
}

