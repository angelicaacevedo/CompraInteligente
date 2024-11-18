package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.MonthlySummaryState
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.UserProgressState
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.LoadingAnimation
import br.com.angelica.comprainteligente.presentation.viewmodel.HomeViewModel
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.White
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(
    userId: String,
    navController: NavController,
    onUserProfile: () -> Unit,
    viewModel: HomeViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeViewModel.HomeIntent.LoadHomeData(userId))
    }

    Scaffold(
        topBar = { HomeTopAppBar(onUserProfile) },
        bottomBar = { CustomBottomNavigation(navController = navController, userId = userId) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> LoadingContent()
                    state.error != null -> ErrorContent(state.error!!) {
                        viewModel.handleIntent(HomeViewModel.HomeIntent.ClearError)
                    }

                    else -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            state.monthlySummary?.let {
                                MonthlySummaryContent(it)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            if (state.recentPurchases.isNotEmpty()) {
                                RecentPurchasesContent(state.recentPurchases)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            state.userProgress?.let {
                                UserProgressContent(it)
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(onUserProfile: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Compra Inteligente",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                color = White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryBlue
        ),
        actions = {
            IconButton(onClick = onUserProfile) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil de Usuário",
                    tint = White
                )
            }
        }
    )
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingAnimation(message = "Carregando...")
    }
}

@Composable
fun MonthlySummaryContent(monthlySummary: MonthlySummaryState) {
    Column {
        Text("Resumo Mensal", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Total Gasto: R$${"%.2f".format(monthlySummary.totalSpent)}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RecentPurchasesContent(recentPurchases: List<Price>) {
    Column {
        Text("Compras Recentes", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(recentPurchases) { purchase ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        "Produto ID: ${purchase.productId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Preço: R$${"%.2f".format(purchase.price)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun UserProgressContent(userProgress: UserProgressState) {
    Column {
        Text("Progresso do Usuário", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Nível: ${userProgress.level}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Pontos: ${userProgress.points}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = userProgress.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Progresso no nível: ${(userProgress.progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Erro: $message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Tentar Novamente")
        }
    }
}
