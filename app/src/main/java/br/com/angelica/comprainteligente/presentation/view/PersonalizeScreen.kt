package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.viewmodel.PersonalizeViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizeScreen(
    navController: NavController,
    viewModel: PersonalizeViewModel = getViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    var newCategory by remember { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()

    val horizontalPadding = 16.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personalização") },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White
                ),
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = horizontalPadding, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text("Categorias de Produtos", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newCategory,
                    onValueChange = { newCategory = it },
                    placeholder = { Text("Nova Categoria") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newCategory.isNotBlank()) {
                            viewModel.onEvent(
                                PersonalizeViewModel.PersonalizeViewEvent.AddCategory(
                                    newCategory
                                )
                            )
                            newCategory = ""
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    Text("Adicionar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            viewState.categories.forEach { category ->
                CategoryItem(
                    category,
                    onRemove = {
                        viewModel.onEvent(
                            PersonalizeViewModel.PersonalizeViewEvent.RemoveCategory(category)
                        )
                    },
                    horizontalPadding = horizontalPadding
                )
            }

            viewState.errorMessage?.let {
                LaunchedEffect(it) {
                    scaffoldState.snackbarHostState.showSnackbar(it)
                    viewModel.onEvent(PersonalizeViewModel.PersonalizeViewEvent.ClearErrorMessage)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Produtos Favoritos", style = MaterialTheme.typography.titleMedium)
            viewState.favoriteProducts.forEach { product ->
                FavoriteProductItem(product)
            }
        }
    }
}

@Composable
fun CategoryItem(category: String, onRemove: () -> Unit, horizontalPadding: Dp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Black)
            }
        }
    }
}

@Composable
fun FavoriteProductItem(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(product.name, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.Star, contentDescription = "Favorito", tint = Color.Yellow)
    }
}
