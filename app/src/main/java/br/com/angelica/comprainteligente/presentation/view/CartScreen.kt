package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.viewmodel.CartViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel = getViewModel()) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrinho") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50), // Cor Verde Claro para o TopAppBar
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController)
        }
    ) { paddingValues ->
        when (state) {
            is CartViewModel.CartState.Loading -> CircularProgressIndicator(
                modifier = Modifier.padding(
                    paddingValues
                )
            )

            is CartViewModel.CartState.Success -> {
                Column(modifier = Modifier.padding(paddingValues)) {
                    CartList(products = state.products, viewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Lógica de finalização de compra */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Finalizar Compra")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FavoriteProductsSection(products = state.products, viewModel)
                }
            }

            is CartViewModel.CartState.Error -> Text(
                "Error: ${state.message}",
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun CartList(products: List<Product>, viewModel: CartViewModel) {
    LazyColumn {
        items(products) { product ->
            if (!product.isFavorite) {
                ProductCard(product, viewModel)
            }
        }
    }
}

@Composable
fun FavoriteProductsSection(products: List<Product>, viewModel: CartViewModel) {
    Text(
        text = "Produtos Favoritos",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp)
    )
    LazyColumn {
        items(products) { product ->
            if (product.isFavorite) {
                ProductCard(product, viewModel)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, viewModel: CartViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Fundo transparente
        border = BorderStroke(1.dp, Color.LightGray) // Borda bem suave
        // Remover o elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Aumentando o padding para conforto
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Alinhando ícones e texto
        ) {
            // Nome do produto
            Text(
                text = product.name,
                modifier = Modifier.weight(1f), // Ocupa o máximo de espaço possível
                style = MaterialTheme.typography.bodyMedium
            )

            // Ícones de delete e favorite
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.handleIntent(
                            CartViewModel.CartIntent.RemoveProduct(product.id)
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black // Ícone de delete preto
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.handleIntent(
                            CartViewModel.CartIntent.ChangeProductFavoriteStatus(
                                product.id,
                                !product.isFavorite
                            )
                        )
                    }
                ) {
                    Icon(
                        if (product.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = Color.Yellow // Ícone de favorite amarelo
                    )
                }
            }
        }
    }
}