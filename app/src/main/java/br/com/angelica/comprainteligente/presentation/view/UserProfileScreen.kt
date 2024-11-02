package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.R
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun UserProfileScreen(
    userId: String,
    onLogoutClick: () -> Unit,
    navController: NavController,
    viewModel: UserProfileViewModel = getViewModel(),
    authViewModel: AuthViewModel = getViewModel()
) {
    LaunchedEffect(userId) {
        viewModel.loadUserData(userId)
    }

    val userData by viewModel.userData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        bottomBar = { CustomBottomNavigation(navController, userId) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                userData?.let { user ->
                    UserProfileContent(
                        user = user,
                        onSaveClick = { updatedUser ->
                            viewModel.updateUserProfile(userId, updatedUser)
                        },
                        onLogoutClick = {
                            viewModel.logout()
                            onLogoutClick()
                        },
                        authViewModel = authViewModel // Passando o authViewModel para buscar endereço
                    )
                } ?: run {
                    Text(text = "Erro ao carregar dados do usuário", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun UserProfileContent(
    user: User,
    onSaveClick: (User) -> Unit,
    onLogoutClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }
    var cep by remember { mutableStateOf(user.address.postalCode) }
    var number by remember { mutableStateOf(user.address.number) }
    var street by remember { mutableStateOf(user.address.street) }
    var neighborhood by remember { mutableStateOf(user.address.neighborhood) }
    var city by remember { mutableStateOf(user.address.city) }
    var state by remember { mutableStateOf(user.address.state) }

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
                    /* Tratar falha, mostrar mensagem de erro */
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "Imagem de Perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            IconButton(
                onClick = { isEditing = !isEditing },
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar imagem de perfil",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nome e Email fora do Card, diretamente abaixo da imagem
        Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)

        Spacer(modifier = Modifier.height(16.dp))

        // Card para as demais informações de endereço
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isEditing) {
                    EditableFields(
                        name = name,
                        onNameChange = { name = it },
                        email = email,
                        onEmailChange = { email = it },
                        cep = cep,
                        onCepChange = { cep = it },
                        street = street,
                        neighborhood = neighborhood,
                        city = city,
                        state = state,
                        number = number,
                        onNumberChange = { number = it }
                    )
                } else {
                    DisplayFields(
                        address = "$street, $number\n$neighborhood, $city, $state\nCEP: $cep"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            Button(
                onClick = {
                    onSaveClick(
                        user.copy(
                            username = name,
                            email = email,
                            address = Address(
                                street = street,
                                number = number,
                                neighborhood = neighborhood,
                                city = city,
                                state = state,
                                postalCode = cep
                            )
                        )
                    )
                    isEditing = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Salvar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Red)
        ) {
            Text("Sair", color = Color.Red)
        }
    }
}

@Composable
fun EditableFields(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    cep: String,
    onCepChange: (String) -> Unit,
    street: String,
    neighborhood: String,
    city: String,
    state: String,
    number: String,
    onNumberChange: (String) -> Unit
) {
    CustomTextField(label = "Nome", value = name, onValueChange = onNameChange)
    CustomTextField(label = "Email", value = email, onValueChange = onEmailChange)
    CustomTextField(label = "CEP", value = cep, onValueChange = onCepChange, isNumeric = true)
    CustomTextField(label = "Rua", value = street, onValueChange = {}, enabled = false)
    CustomTextField(label = "Bairro", value = neighborhood, onValueChange = {}, enabled = false)
    CustomTextField(label = "Cidade", value = city, onValueChange = {}, enabled = false)
    CustomTextField(label = "Estado", value = state, onValueChange = {}, enabled = false)
    CustomTextField(label = "Número", value = number, onValueChange = onNumberChange, isNumeric = true)
}

@Composable
fun DisplayFields(address: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Endereço:", fontWeight = FontWeight.Bold)
        Text(
            address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
