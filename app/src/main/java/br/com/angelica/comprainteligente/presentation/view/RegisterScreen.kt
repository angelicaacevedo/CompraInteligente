package br.com.angelica.comprainteligente.presentation.view

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import org.koin.androidx.compose.getViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,  // Função para navegar para a tela de login
    authViewModel: AuthViewModel = getViewModel()
) {
    // Observando o estado de autenticação
    val authState by authViewModel.authState.collectAsState()

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

    var lastSearchedCep by remember { mutableStateOf("") }


    // Verifica o estado de sucesso e navega para a tela "home"
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            onRegisterSuccess()
            authViewModel.resetAuthState()  // Reseta o estado após o sucesso
        }
    }

    // Busca o endereço pelo CEP
    LaunchedEffect(cep) {
        lastSearchedCep = cep  // Atualiza o CEP pesquisado para evitar buscas repetidas

        if (cep.length == 8) {  // Garante que o CEP tenha o tamanho correto antes da busca
            Log.d("RegisterScreen", "CEP possui 8 dígitos. Iniciando busca de endereço.")
            authViewModel.fetchAddressByCep(
                cep = cep,
                onSuccess = { address ->
                    Log.d("RegisterScreen", "Endereço encontrado: $address")
                    street = address.street
                    neighborhood = address.neighborhood
                    city = address.city
                    state = address.state
                },
                onFailure = {
                    Log.e("RegisterScreen", "Falha ao buscar o endereço: $it")
                    errorMessage = "CEP inválido. Verifique e tente novamente."
                }
            )
        } else if (cep.length < 8) {
            lastSearchedCep = ""
            Log.d("RegisterScreen", "CEP não possui 8 dígitos: $cep")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),  // Scroll para telas menores
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Cadastro",
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
                    // Campos do formulário de cadastro
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

                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirmar Senha",
                        isPassword = true,
                        isError = showErrors && (confirmPassword.isEmpty() || password != confirmPassword),
                        errorMessage = "As senhas não coincidem"
                    )

                    CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Nome de usuário",
                        isError = showErrors && username.isEmpty(),
                        errorMessage = "Campo obrigatório"
                    )

                    CustomTextField(
                        value = cep,
                        onValueChange = { cep = it },
                        label = "CEP",
                        isNumeric = true,
                        isError = showErrors && cep.isEmpty(),
                        errorMessage = "Campo obrigatório"
                    )

                    // Campos preenchidos pela API dos Correios
                    CustomTextField(
                        value = street,
                        onValueChange = {},
                        label = "Rua",
                        enabled = false
                    )

                    CustomTextField(
                        value = neighborhood,
                        onValueChange = {},
                        label = "Bairro",
                        enabled = false
                    )

                    CustomTextField(
                        value = city,
                        onValueChange = {},
                        label = "Cidade",
                        enabled = false
                    )

                    CustomTextField(
                        value = state,
                        onValueChange = {},
                        label = "Estado",
                        enabled = false
                    )

                    // O usuário deve preencher o número manualmente
                    CustomTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = "Número",
                        isNumeric = true,
                        isError = showErrors && number.isEmpty(),
                        errorMessage = "Campo obrigatório"
                    )

                    Button(
                        onClick = {
                            showErrors = true  // Mostra erros quando o botão for clicado
                            if (validateRegisterForm(email, password, confirmPassword, username, cep, number)) {
                                val user = User(id = "", username = username, email = email, passwordHash = password, addressId = "")
                                val address = Address(street = street, number = number, neighborhood = neighborhood, city = city, state = state, postalCode = cep)
                                authViewModel.registerUser(user, address)
                            } else {
                                errorMessage = "Preencha todos os campos corretamente"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cadastrar")
                    }

                    errorMessage?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto para navegar para a tela de login
            Text(
                text = "Já tem uma conta? Faça login",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onNavigateToLogin() }  // Navega para a tela de login
                    .padding(8.dp),
                fontSize = 14.sp
            )
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

