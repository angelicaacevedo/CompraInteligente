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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import br.com.angelica.comprainteligente.utils.CustomAlertDialog
import org.koin.androidx.compose.getViewModel

@Composable
fun NewListScreen(
    onBack: () -> Unit,
    userId: String,
    listId: String?,
    listNameArg: String? = null,
    productIdsArg: List<String>? = null,
    onListCreated: () -> Unit,
    navController: NavController,
    viewModel: ProductListViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    var listName by remember {
        mutableStateOf(
            listNameArg ?: ""
        )
    } // preciso passar o valor do argumento da naviegação
    var query by remember { mutableStateOf("") }
    var selectedProductIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedProducts by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Preencher a lista de produtos selecionados se já houver produtos
    LaunchedEffect(productIdsArg) {
        viewModel.initialize(userId)
        if (!productIdsArg.isNullOrEmpty()) {
            selectedProductIds = productIdsArg
            // Carregar os produtos com base nos IDs
            viewModel.handleIntent(
                ProductListViewModel.ProductListIntent.ViewProductsInList(productIdsArg)
            )
        }
    }

    // Atualizar os produtos selecionados com base no estado carregado
    LaunchedEffect(state) {
        if (state is ProductListViewModel.ProductListState.ProductsLoaded) {
            selectedProducts =
                (state as ProductListViewModel.ProductListState.ProductsLoaded).products
        }

        if (state is ProductListViewModel.ProductListState.ListCreated) {
            onListCreated() // volta para tela de historico de listas
            viewModel.resetState() // Reseta o estado para evitar loops
        }
    }

    Scaffold(
        topBar = {
            NewListTopBar(onBack)
        },
        bottomBar = {
            CustomBottomNavigation(navController = navController, userId = userId)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("Nome da Lista") },
                    modifier = Modifier.fillMaxWidth()
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
                    label = { Text("Adicionar Produto") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Sugestões de Produtos
            if (state is ProductListViewModel.ProductListState.SuggestionsLoaded) {
                val suggestions =
                    (state as ProductListViewModel.ProductListState.SuggestionsLoaded).suggestions
                items(suggestions) { product ->
                    if (product.id !in selectedProductIds) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .clickable {
                                    selectedProductIds = selectedProductIds + product.id
                                    selectedProducts = selectedProducts + product
                                    query = "" // Limpa a barra de pesquisa
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                        ) {
                            Text(
                                text = product.name,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Produtos Selecionados
            if (selectedProducts.isNotEmpty()) {
                item {
                    Text(
                        text = "Produtos Selecionados:",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                items(selectedProducts) { product ->
                    SelectedProductItemCard(
                        product = product,
                        onRemoveProduct = { removedProduct ->
                            selectedProducts = selectedProducts.filter { it != removedProduct }
                            selectedProductIds =
                                selectedProductIds.filter { it != removedProduct.id }
                        }
                    )
                }
            } else {
                item { NoProductsSelectedMessage() }

            }

            item { ListButton(viewModel, userId, listId, listName, selectedProductIds) }

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
        title = { Text(text = "Criar Lista", modifier = Modifier.fillMaxWidth()) },
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
            .padding(vertical = 8.dp, horizontal = 16.dp),
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
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            IconButton(onClick = { onRemoveProduct(product) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover produto",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun ListButton(
    viewModel: ProductListViewModel,
    userId: String,
    listId: String?,
    listName: String,
    selectedProductIds: List<String>
) {

    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = {
            if (listName.isNotBlank() && selectedProductIds.isNotEmpty()) {
                viewModel.handleIntent(
                    ProductListViewModel.ProductListIntent.CreateOrUpdateList(
                        listId = listId,
                        name = listName,
                        productIds = selectedProductIds,
                        userId = userId
                    )
                )
            } else {
                showDialog = true
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = if (listId == null) "Criar" else "Salvar",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    if (showDialog) {
        CustomAlertDialog(
            title = "ATENÇÃO",
            message = when {
                listName.isBlank() -> "O nome da lista não pode estar vazio. Por favor, insira um nome."
                selectedProductIds.isEmpty() -> "Por favor, selecione pelo menos um produto."
                else -> "Ocorreu um erro. Verifique as informações e tente novamente."
            },
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false },
            confirmButtonText = "Entendi",
            showDismissButton = false
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

