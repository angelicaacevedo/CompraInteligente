package br.com.angelica.comprainteligente.presentation.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = getViewModel(),
    onNavigateToCreateList: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Carrega as listas de produtos quando o componente é inicializado
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProductListViewModel.ProductListIntent.LoadLists)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historico de Listas", modifier = Modifier.fillMaxWidth()) },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.Black,
                    containerColor = Color.White,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateList) {
                Icon(Icons.Default.Add, contentDescription = "Add List")
            }
        }
    ) { paddingValues ->
        when (state) {
            is ProductListViewModel.ProductListState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProductListViewModel.ProductListState.ListsLoaded -> {
                val lists = (state as ProductListViewModel.ProductListState.ListsLoaded).lists
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(lists) { list ->
                        Text(text = list.name, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            is ProductListViewModel.ProductListState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${(state as ProductListViewModel.ProductListState.Error).message}")
                }
            }

            else -> Unit
        }
    }
}