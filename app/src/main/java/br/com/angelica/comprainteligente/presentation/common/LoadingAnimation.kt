package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.theme.ButtonGreen
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.SecondaryLilac

@Composable
fun LoadingAnimation(message: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Animações individuais para cada círculo
    val scales = List(3) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 800 + (index * 100),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
    }

    val alphas = List(3) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 800 + (index * 100),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
    }

    // Lista de cores para cada círculo
    val circleColors = listOf(PrimaryBlue, SecondaryLilac, ButtonGreen)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .scale(scales[index].value)
                        .alpha(alphas[index].value)
                        .shadow(4.dp, shape = CircleShape, clip = false)
                        .background(
                            color = circleColors[index].copy(alpha = alphas[index].value),
                            shape = CircleShape
                        )
                )
            }
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
