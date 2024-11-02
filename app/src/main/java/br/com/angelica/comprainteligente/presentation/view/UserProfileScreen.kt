package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import br.com.angelica.comprainteligente.presentation.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun UserProfileScreen(
    userId: String,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: UserProfileViewModel = getViewModel()
) {
    // Carrega os dados do usuário assim que a tela é aberta
    LaunchedEffect(userId) {
        viewModel.loadUserData(userId)
    }

    val userData by viewModel.userData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Exibe um indicador de carregamento enquanto os dados do usuário são carregados
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        userData?.let { user ->
            UserProfileContent(
                user = user,
                onEditClick = onEditClick,
                onLogoutClick = onLogoutClick
            )
        } ?: run {
            Text(
                text = "Erro ao carregar dados do usuário",
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun UserProfileContent(
    user: User,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícone do perfil
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Ícone de Perfil",
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Informações do usuário
        UserInfoSection(username = user.username, email = user.email)

        Spacer(modifier = Modifier.height(24.dp))

        // Informações do endereço
        UserAddressSection(user.address)

        Spacer(modifier = Modifier.height(24.dp))

        // Botões de ação
        Button(onClick = onEditClick, Modifier.fillMaxWidth()) {
            Text("Editar Perfil")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onLogoutClick, Modifier.fillMaxWidth()) {
            Text("Sair")
        }
    }
}

@Composable
fun UserInfoSection(username: String, email: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = "Usuário",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(text = username, style = TextStyle(fontSize = 16.sp))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Email",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(text = email, style = TextStyle(fontSize = 16.sp))
    }
}

@Composable
fun UserAddressSection(address: Address) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = "Endereço",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(text = "Rua: ${address.street}, Nº ${address.number}")
        Text(text = "Bairro: ${address.neighborhood}")
        Text(text = "Cidade: ${address.city}, ${address.state}")
        Text(text = "CEP: ${address.postalCode}")
    }
}

