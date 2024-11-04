package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun HistoryListScreen(
    navController: NavController,
    userId: String,
    onNavigateToCreateList: () -> Unit,
    onNavigateToListItems: (String, String, List<String>) -> Unit,
    viewModel: ProductListViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Carrega as listas de produtos apneas quando a tela for carregada pela primeira vez
    LaunchedEffect(Unit) {
        viewModel.initialize(userId)
        viewModel.handleIntent(ProductListViewModel.ProductListIntent.LoadLists(userId))
    }

    Scaffold(
        topBar = {
            ProductListTopBar(onNavigateToCreateList)
        },
        bottomBar = {
            CustomBottomNavigation(navController = navController, userId = userId)
        },
    ) { paddingValues ->
        when (state) {
            is ProductListViewModel.ProductListState.Loading -> {
                ProductListLoadingProgress(paddingValues)
            }

            is ProductListViewModel.ProductListState.ListsLoaded -> {
                ProductListCard(state, paddingValues, viewModel, onNavigateToListItems, userId)
            }

            is ProductListViewModel.ProductListState.Error -> {
                ProductListErrorMessage(paddingValues, state)
            }

            else -> {
                EnhancedEmptyProductListMessage(paddingValues)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ProductListTopBar(onNavigateToCreateList: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Histórico de Listas",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            IconButton(onClick = { onNavigateToCreateList() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Lista",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun ProductListLoadingProgress(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ProductListCard(
    state: ProductListViewModel.ProductListState,
    paddingValues: PaddingValues,
    viewModel: ProductListViewModel,
    onNavigateToListItems: (String, String, List<String>) -> Unit,
    userId: String
) {
    val lists = (state as ProductListViewModel.ProductListState.ListsLoaded).lists
    if (lists.isEmpty()) {
        EnhancedEmptyProductListMessage(paddingValues)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(lists) { list ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .clickable {
                            onNavigateToListItems(
                                list.id,
                                list.name,
                                list.productIds
                            )
                        },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = list.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = {
                            viewModel.handleIntent(
                                ProductListViewModel.ProductListIntent.DeleteList(list.id, userId)
                            )
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Deletar Lista",
                                tint = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductListErrorMessage(
    paddingValues: PaddingValues,
    state: ProductListViewModel.ProductListState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Erro: ${(state as ProductListViewModel.ProductListState.Error).message}",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EnhancedEmptyProductListMessage(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Carrinho vazio",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Nenhuma lista adicionada!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Toque no botão para adicionar uma nova lista.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
