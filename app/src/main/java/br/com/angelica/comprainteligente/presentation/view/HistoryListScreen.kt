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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun HistoryListScrren(
    onBack: () -> Unit,
    onNavigateToCreateList: () -> Unit,
    onNavigateToListItems: (List<String>) -> Unit,
    viewModel: ProductListViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Carrega as listas de produtos apneas quando a tela for carregada pela primeira vez
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProductListViewModel.ProductListIntent.LoadLists)
    }

    Scaffold(
        topBar = {
            ProductListTopBar(onBack, onNavigateToCreateList)
        }
    ) { paddingValues ->
        when (state) {
            is ProductListViewModel.ProductListState.Loading -> {
                ProductListLoadingProgress(paddingValues)
            }

            is ProductListViewModel.ProductListState.ListsLoaded -> {
                ProductListCard(state, paddingValues, viewModel,onNavigateToListItems)
            }

            is ProductListViewModel.ProductListState.Error -> {
                ProductListErrorMessage(paddingValues, state)
            }

            else -> {
                EmptyProductListMessage(paddingValues)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ProductListTopBar(onBack: () -> Unit, onNavigateToCreateList: () -> Unit) {
    TopAppBar(
        title = {
            Text("Histórico de Listas", modifier = Modifier.fillMaxWidth())
        },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        actions = {
            // Adiciona o ícone de adicionar no lado direito da AppBar
            IconButton(onClick = { onNavigateToCreateList() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Lista"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = Color.Black,
            containerColor = Color.White,
        )
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
        CircularProgressIndicator()
    }
}

@Composable
private fun ProductListCard(
    state: ProductListViewModel.ProductListState,
    paddingValues: PaddingValues,
    viewModel: ProductListViewModel,
    onNavigateToListItems: (List<String>) -> Unit  // Novo parâmetro para navegação
) {
    val lists = (state as ProductListViewModel.ProductListState.ListsLoaded).lists
    if (lists.isEmpty()) {
        EmptyProductListMessage(paddingValues)
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
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onNavigateToListItems(list.productIds) },  // Clique para abrir a lista de produtos
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = list.name, style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = {
                            viewModel.handleIntent(
                                ProductListViewModel.ProductListIntent.DeleteList(list.id)
                            )
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Deletar Lista")
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
        Text("Error: ${(state as ProductListViewModel.ProductListState.Error).message}")
    }
}

@Composable
private fun EmptyProductListMessage(paddingValues: PaddingValues) {
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
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Nenhuma lista adicionada",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Toque no botão para adicionar uma nova lista",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
