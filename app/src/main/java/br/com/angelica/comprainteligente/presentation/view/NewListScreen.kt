package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import br.com.angelica.comprainteligente.utils.CustomAlertDialog
import org.koin.androidx.compose.getViewModel

@Composable
fun NewListScreen(
    onBack: () -> Unit,
    viewModel: ProductListViewModel = getViewModel(),
    onListCreated: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var listName by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var selectedProductIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedProducts by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Verifica se a lista foi criada com sucesso e dispara a navegação
    LaunchedEffect(state) {
        if (state is ProductListViewModel.ProductListState.ListCreated && (state as ProductListViewModel.ProductListState.ListCreated).success) {
            onListCreated()
            viewModel.resetState() // Reseta o estado após a navegação
        }
    }

    Scaffold(
        topBar = {
            NewListTopBar(onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("Nome da Lista") }
                )
            }

            item {
                OutlinedTextField(
                    value = query.uppercase(),
                    onValueChange = {
                        query = it
                        viewModel.handleIntent(
                            ProductListViewModel.ProductListIntent.GetProductSuggestions(it)
                        )
                    },
                    label = { Text("Adicionar Produto") }
                )
            }

            // Exibir sugestões de produtos e permitir a seleção de múltiplos itens
            if (state is ProductListViewModel.ProductListState.SuggestionsLoaded) {
                val suggestions =
                    (state as ProductListViewModel.ProductListState.SuggestionsLoaded).suggestions
                items(suggestions) { product ->
                    Text(
                        text = product.name,
                        modifier = Modifier.clickable {
                            // Armazenamos os IDs dos produtos selecionados
                            selectedProductIds = selectedProductIds + product.id
                            // Adicionamos o produto à lista de produtos selecionados
                            selectedProducts = selectedProducts + product
                            query = "" // Limpa a barra de pesquisa
                        }
                    )
                }
            }

            // Lista de produtos selecionados
            if (selectedProducts.isNotEmpty()) {
                item {
                    Text(
                        text = "Produtos selecionados:",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                items(selectedProducts) { product ->
                    SelectedProductItemCard(product) { removedProduct ->
                        // Remover o produto da lista de produtos e IDs selecionados
                        selectedProducts = selectedProducts.filter { it != removedProduct }
                        selectedProductIds =
                            selectedProductIds.filter { it != removedProduct.id }
                    }
                }
            } else {
                item { NoProductsSelectedMessage() }
            }

            item { CreateListButton(viewModel, listName, selectedProductIds) }

            if (state is ProductListViewModel.ProductListState.ListCreated && (state as ProductListViewModel.ProductListState.ListCreated).success) {
                onListCreated()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NewListTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Crie uma Nova Lista", modifier = Modifier.fillMaxWidth()) },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Voltar"
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
private fun SelectedProductItemCard(
    product: Product,
    onRemoveProduct: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Aqui o texto será quebrado em várias linhas se for muito longo
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)  // Faz com que o texto ocupe o espaço disponível no Row
                    .padding(end = 8.dp)  // Adiciona um espaçamento para o ícone
            )

            IconButton(onClick = { onRemoveProduct(product) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover produto"
                )
            }
        }
    }
}

@Composable
private fun CreateListButton(
    viewModel: ProductListViewModel,
    listName: String,
    selectedProductIds: List<String>
) {

    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = {
            if (listName.isNotBlank() && selectedProductIds.isNotEmpty()) {
                viewModel.handleIntent(
                    ProductListViewModel.ProductListIntent.CreateNewList(
                        listName,
                        selectedProductIds
                    )
                )
            } else {
                showDialog = true
            }
        },
        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
    ) {
        Text("Criar Lista")
    }

    if (showDialog) {
        CustomAlertDialog(
            title = "ATENÇÃO",
            message = if (listName.isBlank()) {
                "O nome da lista não pode estar vazio. Por favor, insira um nome."
            } else {
                "Por favor, selecione pelo menos um produto."
            },
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false },
            confirmButtonText = "Entendi",
            dismissButtonText = "Cancelar"
        )
    }
}

@Composable
private fun NoProductsSelectedMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Nenhum produto selecionado", style = MaterialTheme.typography.bodyMedium)
    }
}

