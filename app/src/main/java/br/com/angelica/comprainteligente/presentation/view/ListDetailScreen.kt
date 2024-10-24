package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import coil.annotation.ExperimentalCoilApi
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun ListDetailScreen(
    productIds: List<String>,
    viewModel: ProductListViewModel = getViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProductsFromList(productIds)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produtos da Lista") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (state) {
            is ProductListViewModel.ProductListState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }

            is ProductListViewModel.ProductListState.ProductsLoaded -> {
                val products =
                    (state as ProductListViewModel.ProductListState.ProductsLoaded).products
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(products) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = product.name)
                        }
                    }
                }
            }

            is ProductListViewModel.ProductListState.Error -> {
                Text(text = "Erro: ${(state as ProductListViewModel.ProductListState.Error).message}")
            }

            else -> {
                Text(text = "Nenhum produto encontrado")
            }
        }
    }
}