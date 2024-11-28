package br.com.angelica.comprainteligente.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingSlideData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color
)
