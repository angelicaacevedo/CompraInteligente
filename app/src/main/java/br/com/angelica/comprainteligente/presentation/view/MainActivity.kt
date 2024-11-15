package br.com.angelica.comprainteligente.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.angelica.comprainteligente.data.SessionManager
import br.com.angelica.comprainteligente.presentation.navigation.AppNavigation
import br.com.angelica.comprainteligente.theme.CompraInteligenteTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    // Inject SessionManager with Koin
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompraInteligenteTheme {
                // Passa a instância de SessionManager para a navegação
                AppNavigation(sessionManager)
            }
        }
    }
}
