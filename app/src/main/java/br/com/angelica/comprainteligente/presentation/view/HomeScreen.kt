package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val homeViewModel: HomeViewModel = getViewModel()
    val state by homeViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.handleIntent(HomeViewModel.HomeIntent.LoadFeaturedProducts)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compra Inteligente") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50), // Cor Verde Claro para o TopAppBar
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo de busca
            SearchBar(searchQuery) { searchQuery = it }
            Spacer(modifier = Modifier.height(16.dp))

            // Título de Destaques
            Text("Destaques da Semana", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Seção de produtos em destaque
            FeaturedProductsSection(state)
        }
    }
}

@Composable
fun SearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text("Pesquisar") },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(15.dp)),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Pesquisar")
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Gray,
            cursorColor = Color.Black,
        )
    )
}

@Composable
fun FeaturedProductsSection(state: HomeViewModel.HomeState) {
    when (state) {
        is HomeViewModel.HomeState.Loading -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Carregando produtos...")
            }
        }

        is HomeViewModel.HomeState.FeatureProductsLoaded -> {
            val products = state.products
            if (products.isEmpty()) {
                Text("Nenhum produto em destaque no momento.")
            } else {
                LazyRow {
                    items(products) { product ->
                        ProductCard(product)
                    }
                }
            }
        }

        is HomeViewModel.HomeState.Error -> {
            Text("Erro: ${(state as HomeViewModel.HomeState.Error).message}", color = Color.Red)
        }

        else -> {}
    }
}

@Composable
fun ProductCard(product: Product) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(160.dp)
            .background(
                Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            ) // Cor clara para o Card
            .shadow(4.dp)
            .clickable { /* Ação ao clicar no produto */ }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(product.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(4.dp))
        Text("R$ ${product.price}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}