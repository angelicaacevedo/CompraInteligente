package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.model.ProductWithLatestPrice
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.EmptyStateScreen
import br.com.angelica.comprainteligente.presentation.common.LoadingAnimation
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import br.com.angelica.comprainteligente.theme.BlueSoft
import br.com.angelica.comprainteligente.theme.TextAccent
import br.com.angelica.comprainteligente.theme.TextBlack
import br.com.angelica.comprainteligente.theme.TextGray
import br.com.angelica.comprainteligente.theme.TextGreen
import br.com.angelica.comprainteligente.theme.White
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
    var specificSupermarket by remember { mutableStateOf<String?>(null) }
    var totalPrice by remember { mutableStateOf(0.0) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isAnalyzeButtonVisible by remember { mutableStateOf(true) }
    var segmentSelection by remember { mutableStateOf("Produtos") }

    LaunchedEffect(state) {
        if (state is ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded) {
            val productsWithPrices =
                (state as ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded).products
            totalPrice = calculateTotalPrice(productsWithPrices, specificSupermarket)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comparação de Preços",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = White
                        )
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
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Exibe o total e o valor em uma linha
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: ",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "R$ ${"%.2f".format(totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = TextGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Campo de seleção de supermercado específico
                DropdownMenuSupermarketSelector(
                    productsWithPrices = if (state is ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded)
                        (state as ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded).products else emptyList(),
                    onSupermarketSelected = { selectedSupermarket ->
                        specificSupermarket = selectedSupermarket
                        totalPrice = calculateTotalPrice(
                            (state as? ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded)?.products.orEmpty(),
                            specificSupermarket
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ListShoppingTextField(
                    selectedList, coroutineScope, sheetState,
                    onListSelected = { isAnalyzeButtonVisible = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Controle de segmentação para alternar visualizações
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf("Produtos", "Supermercados").forEach { segment ->
                        Text(
                            text = segment,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (segment == segmentSelection) White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { segmentSelection = segment }
                                .background(
                                    color = if (segment == segmentSelection) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isAnalyzeButtonVisible) {
                    AnalyzeButton(
                        productListViewModel = productListViewModel,
                        selectedList = selectedList,
                        onAnalyzeClicked = { isAnalyzeButtonVisible = false }
                    )
                }

                // Exibe as listas de preços por produtos ou supermercados
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
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }

                    else -> {
                        EmptyStateScreen(
                            title = "Nenhuma lista selecionada!",
                            message = "Escolha uma lista para comparar os preços mais recentes.",
                            icon = Icons.Outlined.ShoppingCart,
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
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = TextBlack,
                                fontWeight = FontWeight.Bold
                            ),
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
                                            color = if (isSelected) BlueSoft else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedList = list
                                            productListViewModel.resetState()
                                            coroutineScope.launch { sheetState.hide() }
                                        }
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = list.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = TextGray
                                        )
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

fun calculateTotalPrice(
    productsWithPrices: List<ProductWithLatestPrice>,
    specificSupermarket: String? = null
): Double {
    return productsWithPrices
        .filter { specificSupermarket == null || it.supermarket.name == specificSupermarket }
        .sumOf { it.latestPrice.price }
}

@Composable
fun DropdownMenuSupermarketSelector(
    productsWithPrices: List<ProductWithLatestPrice>,
    onSupermarketSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSupermarketName by remember { mutableStateOf<String?>(null) }
    var selectedSupermarketAddress by remember { mutableStateOf<String?>(null) }
    val supermarkets = productsWithPrices.map {
        it.supermarket.name.substringBefore(" - ") to "${it.supermarket.street}, ${it.supermarket.city}, ${it.supermarket.state}, ${it.supermarket.zipCode}"
    }.distinct()

    Column {
        OutlinedTextField(
            value = selectedSupermarketName ?: "Todos os Supermercados",
            onValueChange = {},
            readOnly = true,
            label = { Text("Supermercado") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Selecionar Supermercado"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todos os Supermercados") },
                onClick = {
                    selectedSupermarketName = null
                    selectedSupermarketAddress = null
                    onSupermarketSelected(null)
                    expanded = false
                }
            )

            supermarkets.forEachIndexed { index, (name, address) ->
                if (index > 0) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                DropdownMenuItem(
                    text = {
                        Column {
                            // Nome do Supermercado em negrito (apenas a parte antes do primeiro '-')
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            // Endereço em cinza claro
                            Text(
                                text = address,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    },
                    onClick = {
                        selectedSupermarketName = name  // Exibe apenas o nome selecionado
                        selectedSupermarketAddress =
                            address  // Guarda o endereço completo para exibição
                        onSupermarketSelected(name)
                        expanded = false
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // Exibe o endereço completo abaixo do campo de seleção, se um supermercado for selecionado
        selectedSupermarketAddress?.let { address ->
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
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
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextBlack
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Preço com destaque
                    Text(
                        text = "R$ ${item.latestPrice.price}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextGreen
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Nome do Supermercado com estilo sutil
                    Text(
                        text = item.supermarket.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextAccent
                        )
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
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Exibir endereço completo do supermercado
                    products.firstOrNull()?.let { product ->
                        val address = product.supermarket
                        Text(
                            text = "${address.street}, ${address.city}, ${address.state}, ${address.zipCode}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    products.forEach { product ->
                        Text(
                            text = buildAnnotatedString {
                                append("- ${product.product.name}: ")
                                withStyle(
                                    style = SpanStyle(
                                        color = TextGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) { // Altere Color.Green para a cor desejada
                                    append("R$ ${product.latestPrice.price}")
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
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
        enabled = selectedList != null,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Text(
            "Analisar",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
