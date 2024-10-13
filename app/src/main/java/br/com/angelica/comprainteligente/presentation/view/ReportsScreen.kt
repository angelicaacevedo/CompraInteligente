package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.ReportsViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {
    val reportsViewModel: ReportsViewModel = getViewModel()
    val state by reportsViewModel.state.collectAsState()

    var productName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatórios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
                CustomTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = "Nome do Produto",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        reportsViewModel.loadPriceHistory(productName)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Carregar Histórico de Preços")
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (state) {
                    is ReportsViewModel.ReportsState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                    }
                    is ReportsViewModel.ReportsState.Success -> {
                        val priceHistory = (state as ReportsViewModel.ReportsState.Success).priceHistory
                        PriceHistoryChart(priceHistory)
                    }
                    is ReportsViewModel.ReportsState.Error -> {
                        Text(
                            (state as ReportsViewModel.ReportsState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun PriceHistoryChart(priceHistory: List<Product>) {
    // Aqui você pode usar uma biblioteca de gráficos, como MPAndroidChart ou Compose Charts, para exibir o gráfico de histórico de preços
    // Exemplo simplificado:
    Column {
        Text("Histórico de Preços", style = MaterialTheme.typography.bodySmall)
        priceHistory.forEach { product ->
            Text("Data: ${product.timestamp}, Preço: R$ ${product.price}")
        }
    }
}