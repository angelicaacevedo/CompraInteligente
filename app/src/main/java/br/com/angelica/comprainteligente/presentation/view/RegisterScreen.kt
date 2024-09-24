package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.presentation.viewmodel.RegisterViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
) {
    val registerViewModel: RegisterViewModel = getViewModel()
    val state by registerViewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        var isValid = true
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Email inválido"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank() || password.length < 6) {
            passwordError = "Senha deve ter no mínimo 6 caracteres"
            isValid = false
        } else {
            passwordError = null
        }
        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null
        )
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null
        )
        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (validateForm()) {
                    registerViewModel.processIntent(
                        RegisterViewModel.RegisterIntent.Register(
                            email,
                            password,
                            confirmPassword
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            buildAnnotatedString {
                append("Já tem uma conta? ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Login")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("login")
                },
            textAlign = TextAlign.Center
        )

        when (state) {
            is RegisterViewModel.RegisterState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            is RegisterViewModel.RegisterState.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            }

            is RegisterViewModel.RegisterState.Error -> {
                Text(
                    (state as RegisterViewModel.RegisterState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            else -> {}
        }
    }
}