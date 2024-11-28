package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.SecondaryLilac
import br.com.angelica.comprainteligente.theme.TextGray

@Composable
fun EmptyStateScreen(
    title: String,
    message: String,
    icon: ImageVector,
    contentDescription: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = PrimaryBlue,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )
        }
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = SecondaryLilac,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        item {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextGray.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
