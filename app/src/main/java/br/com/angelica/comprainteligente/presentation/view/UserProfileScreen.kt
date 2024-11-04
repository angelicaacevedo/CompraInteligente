package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.R
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.UserProfileViewModel
import br.com.angelica.comprainteligente.utils.CustomAlertDialog
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val isModalOpen = remember { mutableStateOf(false) }
    val showAlertDialog = remember { mutableStateOf(false) }
    val alertMessage = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil do Usuário") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                userData?.let { user ->
                    UserProfileContent(
                        user = user,
                        onEditProfileClick = { isModalOpen.value = true },
                        onLogoutClick = onLogoutClick
                    )
                } ?: run {
                    Text(
                        text = "Erro ao carregar dados do usuário",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (isModalOpen.value) {
        EditProfileModal(
            user = userData ?: User("", "", "", "", Address()),
            onDismiss = { isModalOpen.value = false },
            onSaveClick = { updatedUser ->
                viewModel.updateUserProfile(userId, updatedUser)
                isModalOpen.value = false
                alertMessage.value = "Informações atualizadas com sucesso!"
                showAlertDialog.value = true
            },
            authViewModel = authViewModel,
        )
    }

    if (showAlertDialog.value) {
        CustomAlertDialog(
            title = "Atualização de Perfil",
            message = alertMessage.value,
            onDismiss = { showAlertDialog.value = false },
            onConfirm = { showAlertDialog.value = false },
            confirmButtonText = "OK",
            showDismissButton = false
        )
    }
}

@Composable
fun UserProfileContent(
    user: User,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.cara),
                contentDescription = "Imagem de Perfil",
                modifier = Modifier.size(100.dp)
            )
            IconButton(
                onClick = {/*Abrir camera*/},
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Editar imagem de perfil",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nome e email
        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Botão de Editar Perfil
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onEditProfileClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.1f
                )
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(50)
        ) {
            Text(text = "Editar Perfil", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Lista de opções
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                ProfileOption(icon = Icons.Default.Favorite, label = "Favoritos")
                ProfileOption(icon = Icons.Default.Language, label = "Idioma")
                ProfileOption(icon = Icons.Default.LocationOn, label = "Localização")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Botão de Logout
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    border = BorderStroke(1.dp, Color.Red),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun ProfileOption(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { /* Ação para cada opção */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileModal(
    user: User,
    onDismiss: () -> Unit,
    onSaveClick: (User) -> Unit,
    authViewModel: AuthViewModel
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showAlertDialog = remember { mutableStateOf(false) }
    val alertMessage = remember { mutableStateOf("") }

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
                    alertMessage.value = "Erro ao buscar endereço. Verifique o CEP."
                    showAlertDialog.value = true
                }
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)  // Define uma altura máxima para o conteúdo do modal
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar Perfil",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    TextButton(
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
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "Salvar",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            item {
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
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
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
    CustomTextField(
        label = "Número",
        value = number,
        onValueChange = onNumberChange,
        isNumeric = true
    )
}
