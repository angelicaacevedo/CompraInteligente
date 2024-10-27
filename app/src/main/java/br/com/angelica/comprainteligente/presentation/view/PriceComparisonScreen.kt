package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.model.ProductWithLatestPrice
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
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
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Expande o modal para tela cheia
    )
    val coroutineScope = rememberCoroutineScope()

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
                title = { Text(text = "Comparação de Preços", modifier = Modifier.fillMaxWidth()) },
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
                ListShoppingTextField(selectedList, coroutineScope, sheetState)

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para "Analisar" e carregar os preços
                AnalyzeButton(productListViewModel, selectedList)

                when (state) {
                    is ProductListViewModel.ProductListState.Loading -> {
                        LoadingAnimation()
                    }

                    is ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded -> {
                        // Exibe a lista de produtos com preços mais recentes
                        val productsWithPrices =
                            (state as ProductListViewModel.ProductListState.ProductsWithLatestPricesLoaded).products
                        ProductsPriceList(productsWithPrices) // Renderiza a lista de produtos
                    }

                    is ProductListViewModel.ProductListState.Error -> {
                        Text(
                            "Erro ao carregar informações",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    else -> {
                        EmptyStateScreen()
                    }
                }
            }
        }
    )

    if (sheetState.isVisible) {
        LaunchedEffect(sheetState.isVisible) {
            // Recarrega as listas sempre que o modal é aberto
            productListViewModel.handleIntent(
                ProductListViewModel.ProductListIntent.LoadLists(
                    userId
                )
            )
        }
    }

    // ModalBottomSheet que só será aberto quando o usuário clicar no TextField para escolher a lista
    if (sheetState.isVisible) {
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

                    // Exibe a lista de listas disponíveis no Modal
                    when (val currentState = state) {
                        is ProductListViewModel.ProductListState.ListsLoaded -> {
                            items(currentState.lists) { list ->
                                val isSelected = list == selectedList
                                Text(
                                    text = list.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp) // Aumenta o espaçamento vertical
                                        .background(
                                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.15f
                                            ) else Color.Transparent,
                                            shape = RoundedCornerShape(20.dp) // Leve arredondamento apenas para o fundo
                                        )
                                        .padding(
                                            vertical = 16.dp,
                                            horizontal = 16.dp
                                        ) // Espaçamento interno
                                        .clickable {
                                            selectedList = list
                                            productListViewModel.resetState()
                                            coroutineScope.launch { sheetState.hide() }
                                        }
                                )
                            }
                        }

                        is ProductListViewModel.ProductListState.Loading -> {
                            item { CircularProgressIndicator() } // Exibe animação de carregamento no modal
                        }

                        else -> {
                            item {
                                Text(
                                    "Estado inesperado",
                                    color = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListShoppingTextField(
    selectedList: ProductList?,
    coroutineScope: CoroutineScope,
    sheetState: SheetState
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
                        style = MaterialTheme.typography.titleLarge,
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
private fun AnalyzeButton(
    productListViewModel: ProductListViewModel,
    selectedList: ProductList?
) {
    Button(
        onClick = {
            selectedList?.let {
                if (it.productIds.isNotEmpty()) {
                    productListViewModel.handleIntent(
                        ProductListViewModel.ProductListIntent.ViewProductsInList(
                            productIds = it.productIds,
                            loadLatestPrices = true
                        )
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedList != null
    ) {
        Text("Analisar")
    }
}

@Composable
fun LoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Três círculos animados lado a lado
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun EmptyStateScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
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
            text = "Nenhuma lista selecionada!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Escolha uma lista para comparar os preços mais recentes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

