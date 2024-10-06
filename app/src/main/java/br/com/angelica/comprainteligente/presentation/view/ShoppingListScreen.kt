package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import br.com.angelica.comprainteligente.model.SupermarketComparisonResult
import br.com.angelica.comprainteligente.presentation.viewmodel.ListsViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ListsViewModel = getViewModel(),
    onNavigateBack: () -> Unit,
    onAddProductClick: () -> Unit
) {
    val state by viewModel.state.collectAsState() // Estado da lista de produtos
    val priceAnalysisState by viewModel.priceAnalysisState.collectAsState() // Estado da análise de preços
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listas de Compras") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                // Campo de busca para adicionar produto
                SearchOrAddProductField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    onAddProductClick = onAddProductClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Exibição da lista de produtos adicionados
                when (state) {
                    is ListsViewModel.ListState.Loading -> Text("Carregando lista...")
                    is ListsViewModel.ListState.Success -> {
                        val products = (state as ListsViewModel.ListState.Success).products
                        ProductList(products, onRemoveProductClick = { product ->
                            viewModel.removeProduct(product)
                        })
                    }

                    is ListsViewModel.ListState.Error -> Text("Erro: ${(state as ListsViewModel.ListState.Error).message}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão de Analisar Preços
                AnalyzePricesButton(onClick = {
                    val shoppingList =
                        (state as? ListsViewModel.ListState.Success)?.products ?: emptyList()
                    viewModel.analyzePrices(shoppingList)
                })

                Spacer(modifier = Modifier.height(16.dp))

                // Exibição dos resultados da análise de preços
                when (priceAnalysisState) {
                    is ListsViewModel.PriceAnalysisState.Idle -> {}
                    is ListsViewModel.PriceAnalysisState.Success -> {
                        val analysisResult =
                            (priceAnalysisState as ListsViewModel.PriceAnalysisState.Success).result
                        PriceComparisonTable(analysisResult)
                    }

                    is ListsViewModel.PriceAnalysisState.Error -> Text("Erro ao analisar preços: ${(priceAnalysisState as ListsViewModel.PriceAnalysisState.Error).message}")
                }
            }
        }
    )
}

@Composable
fun SearchOrAddProductField(
    value: String,
    onValueChange: (String) -> Unit,
    onAddProductClick: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Buscar ou Adicionar Produto") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
            ),
            shape = RoundedCornerShape(8.dp)
        )
        Button(
            onClick = onAddProductClick,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Produto")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Adicionar Produto")
        }
    }
}

@Composable
fun ProductList(products: List<Product>, onRemoveProductClick: (Product) -> Unit) {
    LazyColumn {
        items(products.size) { index ->
            val product = products[index]
            ProductRow(product, onRemoveProductClick)
        }
    }
}

@Composable
fun ProductRow(product: Product, onRemoveProductClick: (Product) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Preço: R$ ${product.price}", style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = { onRemoveProductClick(product) }) {
            Icon(Icons.Default.Delete, contentDescription = "Remover Produto")
        }
    }
}

@Composable
fun AnalyzePricesButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
    ) {
        Text(text = "Analisar Preços", color = Color.White)
    }
}

@Composable
fun PriceComparisonTable(priceAnalysisResult: List<SupermarketComparisonResult>) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = "Comparação de Preços", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Percorre cada supermercado e exibe o preço total para a lista de compras
        priceAnalysisResult.forEach { result ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = result.supermarketName, style = MaterialTheme.typography.bodyMedium)
                Text(text = "R$ ${result.totalPrice}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Destaque para o supermercado com o menor preço
        val bestSupermarket = priceAnalysisResult.minByOrNull { it.totalPrice }
        bestSupermarket?.let {
            Text(
                text = "Supermercado recomendado: ${it.supermarketName}",
                color = Color.Green,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}