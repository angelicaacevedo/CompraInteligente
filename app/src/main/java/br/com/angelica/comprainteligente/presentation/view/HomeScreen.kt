import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import br.com.angelica.comprainteligente.presentation.common.GenericBottomNavigationBar
import br.com.angelica.comprainteligente.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val homeViewModel: HomeViewModel = getViewModel()
    val state by homeViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.handleIntent(HomeViewModel.HomeIntent.LoadFeaturedProducts)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compra Inteligente") }
            )
        },
        bottomBar = {
            GenericBottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Pesquisar") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Destaques da Semana", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            when (state) {
                is HomeViewModel.HomeState.Loading -> CircularProgressIndicator()
                is HomeViewModel.HomeState.FeatureProductsLoaded -> {
                    val products = (state as HomeViewModel.HomeState.FeatureProductsLoaded).products
                    LazyRow {
                        items(products) { product ->
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.LightGray)
                                    .padding(16.dp)
                            ) {
                                Text(product.name, style = MaterialTheme.typography.bodySmall)
                                Text(product.description)
                                Text("R$ ${product.price}")
                                Text(product.supermarket)
                            }
                        }
                    }
                }

                is HomeViewModel.HomeState.Error -> {
                    Text((state as HomeViewModel.HomeState.Error).message, color = Color.Red)
                }

                else -> {}
            }
        }
    }
}
