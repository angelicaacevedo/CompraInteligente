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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import br.com.angelica.comprainteligente.utils.CustomAlertDialog
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = getViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrors by remember { mutableStateOf(false) }
    var emailCheckLoading by remember { mutableStateOf(false) }
    var emailExists by remember { mutableStateOf(false) }

    // Observa o estado de autenticação e define mensagens de erro quando apropriado
    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Success -> {
                onRegisterSuccess((authState as AuthViewModel.AuthState.Success).userId)
                authViewModel.resetAuthState()
            }

            is AuthViewModel.AuthState.Error -> {
                errorMessage = (authState as AuthViewModel.AuthState.Error).message
                authViewModel.resetAuthState()  // Limpa o estado de erro após exibir a mensagem
            }

            else -> Unit
        }
    }

    LaunchedEffect(cep) {
        if (cep.length == 8) {
            authViewModel.fetchAddressByCep(
                cep = cep,
                onSuccess = { address ->
                    street = address.street
                    neighborhood = address.neighborhood
                    city = address.city
                    state = address.state
                },
                onFailure = {
                    errorMessage = "CEP inválido. Verifique e tente novamente."
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    title = { Text(text = "Cadastro") },
                    navigationIcon = {
                        IconButton(onClick = { /* ação de voltar */ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                        .fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "E-mail",
                            isError = showErrors && email.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email icon"
                                )
                            }
                        )

                        if (emailCheckLoading) {
                            CustomAlertDialog(
                                title = "Verificação de Email",
                                message = "Estamos verificando seu email...",
                                onDismiss = {},
                                onConfirm = {}
                            )
                        }

                        if (emailExists) {
                            CustomAlertDialog(
                                title = "Conta Existente",
                                message = "A conta já existe",
                                onDismiss = { emailExists = false },
                                onConfirm = { emailExists = false }
                            )
                        }

                        CustomTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Senha",
                            isPassword = true,
                            isError = showErrors && password.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Password Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirmar Senha",
                            isPassword = true,
                            isError = showErrors && (confirmPassword.isEmpty() || password != confirmPassword),
                            errorMessage = "As senhas não coincidem",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Confirm Password Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Nome de usuário",
                            isError = showErrors && username.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "User Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = cep,
                            onValueChange = { cep = it },
                            label = "CEP",
                            isNumeric = true,
                            isError = showErrors && cep.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Map,
                                    contentDescription = "CEP Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = street,
                            onValueChange = {},
                            label = "Rua",
                            enabled = false,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Street Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = neighborhood,
                            onValueChange = {},
                            label = "Bairro",
                            enabled = false,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Neighborhood Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = city,
                            onValueChange = {},
                            label = "Cidade",
                            enabled = false,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationCity,
                                    contentDescription = "City Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = state,
                            onValueChange = {},
                            label = "Estado",
                            enabled = false,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Flag,
                                    contentDescription = "State Icon"
                                )
                            }
                        )

                        CustomTextField(
                            value = number,
                            onValueChange = { number = it },
                            label = "Número",
                            isNumeric = true,
                            isError = showErrors && number.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Number Icon"
                                )
                            }
                        )

                        Button(
                            onClick = {
                                showErrors = true
                                if (validateRegisterForm(
                                        email,
                                        password,
                                        confirmPassword,
                                        username,
                                        cep,
                                        number
                                    )
                                ) {
                                    val address = Address(
                                        street = street,
                                        number = number,
                                        neighborhood = neighborhood,
                                        city = city,
                                        state = state,
                                        postalCode = cep
                                    )
                                    authViewModel.registerUser(username, email, password, address)
                                } else {
                                    errorMessage = "Preencha todos os campos corretamente"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cadastrar")
                        }

                        // Exibir mensagem de erro caso ocorra erro no cadastro (como email já registrado)
                        errorMessage?.let {
                            CustomAlertDialog(
                                title = "Erro",
                                message = it,
                                onDismiss = { errorMessage = null },
                                onConfirm = { errorMessage = null }
                            )
                        }

                    }
                }

                if (authState is AuthViewModel.AuthState.Success) {
                    CustomAlertDialog(
                        title = "Cadastro Realizado",
                        message = "Cadastro realizado com sucesso!",
                        onDismiss = { /* ação após sucesso */ },
                        onConfirm = { onNavigateToLogin() },
                        confirmButtonText = "Prosseguir"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Já tem uma conta? Faça login",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(8.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun validateRegisterForm(
    email: String,
    password: String,
    confirmPassword: String,
    username: String,
    cep: String,
    number: String
): Boolean {
    return email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
            password == confirmPassword && username.isNotEmpty() && cep.isNotEmpty() && number.isNotEmpty()
}
