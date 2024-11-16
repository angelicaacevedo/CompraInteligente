package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.angelica.comprainteligente.theme.BlueSoft
import br.com.angelica.comprainteligente.theme.ButtonGreen
import br.com.angelica.comprainteligente.theme.ButtonRed
import br.com.angelica.comprainteligente.theme.GreenStrong
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.RedSoft
import br.com.angelica.comprainteligente.theme.TextGray
import br.com.angelica.comprainteligente.theme.White

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String = "Ok",
    dismissButtonText: String = "Cancelar",
    showDismissButton: Boolean = true,
    backgroundColor: Color = White,
    titleColor: Color = PrimaryBlue,
    messageColor: Color = TextGray,
    buttonTextColor: Color = Color.White
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = titleColor,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(color = messageColor),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (showDismissButton) {
                        TextButton(
                            onClick = { onDismiss() },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ButtonRed,
                                contentColor = buttonTextColor
                            )
                        ) {
                            Text(
                                text = dismissButtonText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Button(
                        onClick = { onConfirm() },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonGreen,
                            contentColor = buttonTextColor
                        )
                    ) {
                        Text(
                            text = confirmButtonText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
