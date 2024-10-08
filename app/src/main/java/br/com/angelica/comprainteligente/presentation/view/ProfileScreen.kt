package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.NotificationPreferences
import br.com.angelica.comprainteligente.model.UserProfile
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.ProfileViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val profileViewModel: ProfileViewModel = getViewModel()
    val state by profileViewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var preferredSupermarket by remember { mutableStateOf("") }
    var promotions by remember { mutableStateOf(false) }
    var priceAlerts by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is ProfileViewModel.ProfileState.Success) {
            val userProfile = (state as ProfileViewModel.ProfileState.Success).userProfile
            name = userProfile.name
            email = userProfile.email
            preferredSupermarket = userProfile.preferredSupermarket
            promotions = userProfile.notificationPreferences.promotions
            priceAlerts = userProfile.notificationPreferences.priceAlerts
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Informações do usuário
                Text("Informações do Usuário", style = MaterialTheme.typography.bodySmall)
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nome",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = preferredSupermarket,
                    onValueChange = { preferredSupermarket = it },
                    label = "Supermercado Preferido",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Alterar senha
                Text("Alterar Senha", style = MaterialTheme.typography.bodySmall)
                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Nova Senha",
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Configurações de notificação
                Text("Configurações de Notificação", style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = promotions,
                        onCheckedChange = { promotions = it }
                    )
                    Text("Promoções")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = priceAlerts,
                        onCheckedChange = { priceAlerts = it }
                    )
                    Text("Alertas de Preços")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Histórico de interações
                Text("Histórico de Interações", style = MaterialTheme.typography.bodySmall)
                // Aqui você pode adicionar uma lista para mostrar o histórico de interações

                Spacer(modifier = Modifier.height(16.dp))

                // Favoritos
                Text("Favoritos", style = MaterialTheme.typography.bodySmall)
                // Aqui você pode adicionar uma lista para mostrar os supermercados e produtos favoritos

                Spacer(modifier = Modifier.height(16.dp))

                // Nível de contribuição
                Text("Nível de Contribuição", style = MaterialTheme.typography.bodySmall)
                // Aqui você pode adicionar uma visualização para mostrar o nível de contribuição e as medalhas

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val userProfile = UserProfile(
                            userId = profileViewModel.state.value.let { (it as? ProfileViewModel.ProfileState.Success)?.userProfile?.userId ?: "" },
                            name = name,
                            email = email,
                            preferredSupermarket = preferredSupermarket,
                            notificationPreferences = NotificationPreferences(promotions, priceAlerts)
                        )
                        profileViewModel.updateUserProfile(userProfile)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Atualizar Perfil")
                }

                when (state) {
                    is ProfileViewModel.ProfileState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                    }
                    is ProfileViewModel.ProfileState.Error -> {
                        Text(
                            (state as ProfileViewModel.ProfileState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    else -> {}
                }
            }
        }
    )
}