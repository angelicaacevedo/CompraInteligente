package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.presentation.viewmodel.MainViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.Purchase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, mainViewModel: MainViewModel = viewModel()) {
    val purchases by mainViewModel.purchases.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Compras") },
                actions = {
                    IconButton(onClick = { /* Navegar para perfil */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                    IconButton(onClick = { /* Navegar para configurações */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addPurchase") }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Compra")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(purchases) { purchase ->
                PurchaseItem(purchase, onClick = { /* Navegar para detalhes/edição */ })
            }
        }
    }
}

@Composable
fun PurchaseItem(purchase: Purchase, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = purchase.name, style = MaterialTheme.typography.bodySmall)
            Text(text = "Quantidade: ${purchase.quantity}")
            Text(text = "Preço: ${purchase.price}")
            Text(text = "Categoria: ${purchase.category}")
        }
    }
}