package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ListDetailScreen(
    userId: String,
    listId: String,
    productIds: List<String>,
    listName: String,
    onBack: () -> Unit,
    onEditList: (String, String, List<String>) -> Unit,
    navController: NavController,
    viewModel: ProductListViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(productIds) {
        viewModel.initialize(userId)
        viewModel.handleIntent(
            ProductListViewModel.ProductListIntent.ViewProductsInList(
                userId,
                productIds
            )
        )
    }

    Scaffold(
        topBar = {
            ListDetailTopBar(
                onBack = onBack,
                onEdit = { onEditList(listId, listName, productIds) }
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController = navController, userId = userId)
        },
    ) { paddingValues ->
        when (state) {
            is ProductListViewModel.ProductListState.Loading -> {
                ListDetailCircularProgress()
            }

            is ProductListViewModel.ProductListState.ProductsLoaded -> {
                ProductListDetailCard(state, paddingValues)
            }

            is ProductListViewModel.ProductListState.Empty -> {
                EmptyListDetailMessage(paddingValues)
            }

            is ProductListViewModel.ProductListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Erro: ${(state as ProductListViewModel.ProductListState.Error).message}")
                }
            }

            else -> {}
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ListDetailTopBar(
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Produtos da Lista",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Voltar")
            }
        },
        actions = {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Lista")
            }
        }
    )
}

@Composable
private fun ListDetailCircularProgress() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}

@Composable
private fun ProductListDetailCard(
    state: ProductListViewModel.ProductListState,
    paddingValues: PaddingValues
) {
    val products = (state as ProductListViewModel.ProductListState.ProductsLoaded).products

    if (products.isEmpty()) {
        EmptyListDetailMessage(paddingValues)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                        // Aqui você pode adicionar um botão de ação, como deletar ou editar o produto
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyListDetailMessage(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Nenhuma produto na Lista",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}