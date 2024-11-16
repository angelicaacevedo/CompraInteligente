package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.angelica.comprainteligente.presentation.common.CustomAlertDialog
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import br.com.angelica.comprainteligente.theme.GraySoft
import br.com.angelica.comprainteligente.theme.GreenStrong
import br.com.angelica.comprainteligente.theme.HighlightYellow
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.TextGray
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = getViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrors by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            val userId = (authState as AuthViewModel.AuthState.Success).userId
            onLoginSuccess(userId)
            authViewModel.resetAuthState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GraySoft),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center,
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(0.85f),
                    colors = CardDefaults.cardColors(containerColor = GraySoft)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextField(
                            value = email,
                            onValueChange = { email = it.trim() },
                            label = "E-mail",
                            isError = showErrors && email.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            isPassword = false,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = TextGray
                                )
                            }
                        )

                        CustomTextField(
                            value = password,
                            onValueChange = { password = it.trim() },
                            label = "Senha",
                            isPassword = true,
                            showPassword = showPassword,
                            isError = showErrors && password.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = TextGray
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = null,
                                        tint = TextGray
                                    )
                                }
                            }
                        )

                        Button(
                            onClick = {
                                showErrors = true
                                if (validateLoginForm(email, password)) {
                                    authViewModel.loginUser(email, password)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Autenticando...")
                                    }
                                } else {
                                    errorMessage = "Preencha todos os campos"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "Entrar",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Não tem uma conta? Cadastre-se",
                    color = PrimaryBlue,
                    modifier = Modifier
                        .clickable { onNavigateToRegister() }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            errorMessage?.let {
                CustomAlertDialog(
                    title = "Atenção",
                    message = it,
                    onDismiss = { errorMessage = null },
                    onConfirm = {}
                )
            }

            if (authState is AuthViewModel.AuthState.Error) {
                val message = (authState as AuthViewModel.AuthState.Error).message
                CustomAlertDialog(
                    title = "Erro",
                    message = message,
                    onDismiss = { authViewModel.resetAuthState() },
                    onConfirm = {}
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

fun validateLoginForm(email: String, password: String): Boolean {
    return email.isNotEmpty() && password.isNotEmpty()
}
