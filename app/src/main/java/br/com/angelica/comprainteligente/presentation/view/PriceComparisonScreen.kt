package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.model.ProductWithLatestPrice
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.EmptyStateScreen
import br.com.angelica.comprainteligente.presentation.common.LoadingAnimation
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceComparisonScreen(
    userId: String,
    navController: NavController,
    productListViewModel: ProductListViewModel = getViewModel()
) {
    val state by productListViewModel.state.collectAsState()
    var selectedList by remember { mutableStateOf<ProductList?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isAnalyzeButtonVisible by remember { mutableStateOf(true) }
    var segmentSelection by remember { mutableStateOf("Produtos") }

    // Carrega listas no inicio se ainda não estiverem carregadas
    LaunchedEffect(Unit) {
        productListViewModel.initialize(userId)
        if (state !is ProductListViewModel.ProductListState.ListsLoaded) {
            productListViewModel.handleIntent(
                ProductListViewModel.ProductListIntent.LoadLists(
                    userId
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comparação de Preços",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController = navController, userId = userId)
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
                // Campo de seleção de lista
                ListShoppingTextField(
                    selectedList,
                    coroutineScope,
                    sheetState,
                    onListSelected = { isAnalyzeButtonVisible = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Controle de segmentação (Segmented Control) para alternar visualizações
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf("Produtos", "Supermercados").forEach { segment ->
                        Text(
                            text = segment,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (segment == segmentSelection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { segmentSelection = segment }
                                .background(
                                    color = if (segment == segmentSelection) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.15f
                                    ) else Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para "Analisar"
                if (isAnalyzeButtonVisible) {
                    AnalyzeButton(
                        productListViewModel = productListViewModel,
                        selectedList = selectedList,
                        onAnalyzeClicked = { isAnalyzeButtonVisible = false }
                    )
                }

                when (state) {
                    is ProductListViewModel.ProductListState.Loading -> {
                        LoadingAnimation(message = "Aguarde, estamos trazendo os dados...")
                    }

                    is ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded -> {
                        val productsWithPrices =
                            (state as ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded).products
                        if (segmentSelection == "Produtos") {
                            ProductsPriceList(productsWithPrices)
                        } else {
                            SupermarketsPriceList(productsWithPrices)
                        }
                    }

                    is ProductListViewModel.ProductListState.Error -> {
                        Text(
                            text = "Erro ao carregar informações",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    else -> {
                        EmptyStateScreen(
                            title = "Nenhuma lista selecionada!",
                            message = "Escolha uma lista para comparar os preços mais recentes.",
                            icon = Icons.Default.ShoppingCart,
                            contentDescription = "Carrinho vazio"
                        )
                    }
                }
            }
        }
    )

    if (sheetState.isVisible) {
        LaunchedEffect(sheetState.isVisible) {
            // Recarrega as listas sempre que o modal é aberto
            productListViewModel.handleIntent(
                ProductListViewModel.ProductListIntent.LoadLists(userId)
            )
        }
    }

    // ModalBottomSheet para selecionar a lista de compras
    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            content = {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            "Escolha uma lista",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    when (val currentState = state) {
                        is ProductListViewModel.ProductListState.ListsLoaded -> {
                            items(currentState.lists) { list ->
                                val isSelected = list == selectedList
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .background(
                                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.15f
                                            ) else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedList = list
                                            productListViewModel.resetState()
                                            coroutineScope.launch { sheetState.hide() }
                                        }
                                        .padding(16.dp) // Espaçamento interno para todo o conteúdo
                                ) {
                                    Text(
                                        text = list.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        else -> item { Text("Carregando listas...") }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListShoppingTextField(
    selectedList: ProductList?,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    onListSelected: () -> Unit
) {
    OutlinedTextField(
        value = selectedList?.name ?: "Selecione uma lista",
        onValueChange = { },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    sheetState.show()
                    onListSelected()
                }
            },
        label = { Text("Lista de Compras") },
        trailingIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    if (sheetState.isVisible) {
                        sheetState.hide()
                    } else {
                        sheetState.show()
                        onListSelected()
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
}

@Composable
fun ProductsPriceList(productsWithPrices: List<ProductWithLatestPrice>) {
    LazyColumn {
        items(productsWithPrices) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Nome do Produto
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Preço com destaque
                    Text(
                        text = "R$ ${item.latestPrice.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Nome do Supermercado com estilo sutil
                    Text(
                        text = item.supermarket.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun SupermarketsPriceList(productsWithPrices: List<ProductWithLatestPrice>) {
    LazyColumn {
        items(productsWithPrices.groupBy { it.supermarket.name }
            .toList()) { (supermarket, products) ->

            // Extrai apenas o nome do supermercado
            val supermarketName = supermarket.split(" - ").firstOrNull() ?: supermarket

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Nome do Supermercado com estilo de título
                    Text(
                        text = supermarketName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Exibir endereço completo do supermercado
                    products.firstOrNull()?.let { product ->
                        val address = product.supermarket
                        Text(
                            text = "${address.street}, ${address.city}, ${address.state}, ${address.zipCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Lista de produtos com preços
                    products.forEach { product ->
                        Text(
                            text = "- ${product.product.name}: R$ ${product.latestPrice.price}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyzeButton(
    productListViewModel: ProductListViewModel,
    selectedList: ProductList?,
    onAnalyzeClicked: () -> Unit
) {
    Button(
        onClick = {
            selectedList?.let {
                if (it.productIds.isNotEmpty()) {
                    productListViewModel.handleIntent(
                        ProductListViewModel.ProductListIntent.ViewProductsInList(
                            userId = it.userId,
                            productIds = it.productIds,
                            loadLatestPrices = true
                        )
                    )
                    onAnalyzeClicked()
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedList != null
    ) {
        Text("Analisar")
    }
}
