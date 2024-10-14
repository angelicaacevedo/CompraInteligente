package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val categories by viewModel.categories.collectAsState()
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var newCategory by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TopAppBar(
            title = { Text("Personalização") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF4CAF50), // Cor Verde Claro para o TopAppBar
                titleContentColor = Color.White
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                        viewModel.addCategory(newCategory)
                        newCategory = ""
                    }
                },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(2.dp)) // Menos arredondado
            ) {
                Text("Adicionar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        categories.forEach { category ->
            CategoryItem(category, onRemove = { viewModel.removeCategory(category) })
        }

        errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Produtos Favoritos", style = MaterialTheme.typography.titleMedium)
        favoriteProducts.forEach { product ->
            FavoriteProductItem(product)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        CustomBottomNavigation(navController)
    }
}

@Composable
fun CategoryItem(category: String, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color(0xFFB4DF9A), // Verde claro
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remover")
            }
        }
    }
}

@Composable
fun FavoriteProductItem(product: Product) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(product.name, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.Star, contentDescription = "Favorito", tint = Color.Yellow)
    }
}
