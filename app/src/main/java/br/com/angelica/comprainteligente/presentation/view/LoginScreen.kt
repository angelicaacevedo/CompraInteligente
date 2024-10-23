package br.com.angelica.comprainteligente.presentation.view

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.getViewModel


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,  // Função para navegar para a tela de cadastro
    authViewModel: AuthViewModel = getViewModel()
) {
    // Observando o estado de autenticação
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrors by remember { mutableStateOf(false) }

    // Verifica o estado de sucesso e navega para a tela "home"
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            onLoginSuccess()
            authViewModel.resetAuthState()  // Reseta o estado após o sucesso
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.85f),  // Define a largura do Card
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        isError = showErrors && email.isEmpty(),
                        errorMessage = "Campo obrigatório"
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha",
                        isPassword = true,
                        isError = showErrors && password.isEmpty(),
                        errorMessage = "Campo obrigatório"
                    )

                    Button(
                        onClick = {
                            showErrors = true  // Mostra erros se o botão for clicado
                            if (validateLoginForm(email, password)) {
                                authViewModel.loginUser(email, password)
                            } else {
                                errorMessage = "Preencha todos os campos"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Entrar")
                    }

                    errorMessage?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto para navegar para a tela de cadastro
            Text(
                text = "Não tem uma conta? Cadastre-se",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onNavigateToRegister() }  // Navega para a tela de cadastro
                    .padding(8.dp),
                fontSize = 14.sp
            )
        }
    }
}

fun validateLoginForm(email: String, password: String): Boolean {
    return email.isNotEmpty() && password.isNotEmpty()
}
