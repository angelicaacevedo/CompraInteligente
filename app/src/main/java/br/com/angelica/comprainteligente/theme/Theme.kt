package br.com.angelica.comprainteligente.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GreenLight,
    secondary = BlueGrayLight,
    background = NeutralGrayLight,
    surface = SurfaceGrayLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF455A64),
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenDark,
    secondary = BlueGrayDark,
    background = Color.Black,
    surface = BlueGrayDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun CompraInteligenteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}