package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductDetailsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ProductDetailsScreen(productId: String, viewModel: ProductDetailsViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductDetails(productId)
    }

    when (state) {
        is ProductDetailsViewModel.ProductDetailState.Loading -> {
            CircularProgressIndicator()
        }

        is ProductDetailsViewModel.ProductDetailState.Success -> {
            val product = (state as ProductDetailsViewModel.ProductDetailState.Success).product
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = product.name)
                Text(text = product.description)
                Text(text = "Preço: ${product.price}")
                Text(text = "Supermercado: ${product.supermarket}")
            }
        }

        is ProductDetailsViewModel.ProductDetailState.Error -> {
            val message = (state as ProductDetailsViewModel.ProductDetailState.Error).message
            Text(text = message)

        }
    }
}