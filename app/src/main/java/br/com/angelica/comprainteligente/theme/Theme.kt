package br.com.angelica.comprainteligente.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2E7D32), // Verde Escuro
    secondary = Color(0xFFFFA726), // Laranja
    tertiary = Color(0xFFB0BEC5)   // Cinza
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50), // Verde Claro
    secondary = Color(0xFFFFB74D), // Laranja Claro
    tertiary = Color(0xFFE0E0E0)   // Cinza Claro
)


@Composable
fun CompraInteligenteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
