package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceComparisonScreen(
    onBackClick: () -> Unit,
    productListViewModel: ProductListViewModel = getViewModel()
) {
    val state by productListViewModel.state.collectAsState()
    var selectedList by remember { mutableStateOf<ProductList?>(null) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Expande o modal para tela cheia
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        productListViewModel.handleIntent(ProductListViewModel.ProductListIntent.LoadLists)
    }

    // ModalBottomSheet que só será aberto quando o usuário clicar no TextField
    if (sheetState.isVisible) { // Modal só aparece quando 'isVisible' é true
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            "Escolha uma lista",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    when (state) {
                        is ProductListViewModel.ProductListState.ListsLoaded -> {
                            val lists =
                                (state as ProductListViewModel.ProductListState.ListsLoaded).lists

                            items(lists) { list ->
                                ListItem(
                                    headlineContent = { Text(list.name) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedList = list
                                            coroutineScope.launch { sheetState.hide() }
                                        }
                                )
                            }
                        }

                        is ProductListViewModel.ProductListState.Error -> {
                            item {
                                Text(
                                    "Erro ao carregar listas",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparador de Preços") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = selectedList?.name ?: "Selecione uma lista",
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch { sheetState.show() } // Exibe o modal quando o TextField é clicado
                        },
                    label = { Text("Lista de Compras") },
                    trailingIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (sheetState.isVisible) {
                                    sheetState.hide()
                                } else {
                                    sheetState.show()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (sheetState.isVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (sheetState.isVisible) "Collapse" else "Expand"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* SEM AÇÃO POR ENQUANTO */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedList != null
                ) {
                    Text("Analisar")
                }
            }
        }
    )
}
