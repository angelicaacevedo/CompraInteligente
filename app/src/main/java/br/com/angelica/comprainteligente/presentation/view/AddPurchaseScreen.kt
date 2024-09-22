package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.presentation.viewmodel.AddPurchaseViewModel

@Composable
fun AddPurchaseScreen(navController: NavController, addPurchaseViewModel: AddPurchaseViewModel = viewModel()) {
    val name by addPurchaseViewModel.name.collectAsState()
    val quantity by addPurchaseViewModel.quantity.collectAsState()
    val price by addPurchaseViewModel.price.collectAsState()
    val category by addPurchaseViewModel.category.collectAsState()
    val state by addPurchaseViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = name,
            onValueChange = { addPurchaseViewModel.processIntent(AddPurchaseViewModel.AddPurchaseIntent.NameChanged(it)) },
            label = { Text("Nome do Item") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = quantity,
            onValueChange = { addPurchaseViewModel.processIntent(AddPurchaseViewModel.AddPurchaseIntent.QuantityChanged(it)) },
            label = { Text("Quantidade") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = price,
            onValueChange = { addPurchaseViewModel.processIntent(AddPurchaseViewModel.AddPurchaseIntent.PriceChanged(it)) },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = category,
            onValueChange = { addPurchaseViewModel.processIntent(AddPurchaseViewModel.AddPurchaseIntent.CategoryChanged(it)) },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                addPurchaseViewModel.processIntent(AddPurchaseViewModel.AddPurchaseIntent.SavePurchase(name, quantity, price, category))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar")
        }

        when (state) {
            is AddPurchaseViewModel.AddPurchaseState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            is AddPurchaseViewModel.AddPurchaseState.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate("mainScreen") {
                        popUpTo("addPurchase") { inclusive = true }
                    }
                }
            }

            is AddPurchaseViewModel.AddPurchaseState.Error -> {
                Text(
                    text = (state as AddPurchaseViewModel.AddPurchaseState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            else -> {}
        }
    }
}