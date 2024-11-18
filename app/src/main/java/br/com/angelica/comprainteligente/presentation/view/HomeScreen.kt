package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.MonthlySummaryState
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.UserProgressState
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.LoadingAnimation
import br.com.angelica.comprainteligente.presentation.viewmodel.HomeViewModel
import br.com.angelica.comprainteligente.theme.BackgroundLightGray
import br.com.angelica.comprainteligente.theme.BlueSoft
import br.com.angelica.comprainteligente.theme.ButtonGreen
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.TextBlack
import br.com.angelica.comprainteligente.theme.TextGreen
import br.com.angelica.comprainteligente.theme.TextPrimary
import br.com.angelica.comprainteligente.theme.TrophyGold
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
                    state.isLoading -> {
                        LoadingContent()
                    }
                    state.error != null -> {
                        ErrorContent(state.error!!) {
                            viewModel.handleIntent(HomeViewModel.HomeIntent.ClearError)
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            state.userProgress?.let {
                                UserProgressCard(it)
                            }
                            state.monthlySummary?.let {
                                MonthlySummaryCard(it)
                            }
                            if (state.recentPurchases.isNotEmpty()) {
                                RecentPurchasesCard(state.recentPurchases)
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
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil de Usuário",
                    tint = White
                )
            }
        }
    )
}

@Composable
fun UserProgressCard(userProgress: UserProgressState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundLightGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Troféu",
                tint = TrophyGold,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Progresso do Usuário", style = MaterialTheme.typography.headlineSmall.copy(
                color = TextBlack,
                fontWeight = FontWeight.Bold
            ))
            Text("Nível: ${userProgress.level}", style = MaterialTheme.typography.bodyLarge)
            Text("Pontos: ${userProgress.points}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { userProgress.progress },
                modifier = Modifier.fillMaxWidth(),
                color = ButtonGreen,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Progresso no nível: ${(userProgress.progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun MonthlySummaryCard(monthlySummary: MonthlySummaryState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundLightGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumo Mensal", style = MaterialTheme.typography.headlineSmall.copy(
                color = TextBlack,
                fontWeight = FontWeight.Bold
            ))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Gasto: R$${"%.2f".format(monthlySummary.totalSpent)}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextBlack
            )
        }
    }
}

@Composable
fun RecentPurchasesCard(recentPurchases: List<Pair<Price, String>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundLightGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Compras Recentes", style = MaterialTheme.typography.headlineSmall.copy(
                color = TextBlack,
                fontWeight = FontWeight.Bold
            ))
            Spacer(modifier = Modifier.height(8.dp))
            recentPurchases.forEach { (purchase, productName) ->
                ProductCard(purchase, productName)
            }
        }
    }
}

@Composable
fun ProductCard(purchase: Price, productName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = BlueSoft),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = productName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                )
            )
            Text(
                text = "R$${"%.2f".format(purchase.price)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
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
