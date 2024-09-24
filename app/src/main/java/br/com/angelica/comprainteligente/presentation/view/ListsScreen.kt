package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.presentation.viewmodel.ListsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ListsScreen(viewModel: ListsViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (state) {
            is ListsViewModel.ListState.Loading -> {
                CircularProgressIndicator()
            }

            is ListsViewModel.ListState.Success -> {
                val products = (state as ListsViewModel.ListState.Success).products
                Column(modifier = Modifier.padding(16.dp)) {
                    var newProductName by remember { mutableStateOf("") }
                    var newProductDescription by remember { mutableStateOf("") }
                    var newProductQuantity by remember { mutableStateOf("") }

                    BasicTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        decorationBox = { innerTextField ->
                            if (newProductName.isEmpty()) {
                                Text("Nome do Produto")
                            }
                            innerTextField()
                        }
                    )

                    BasicTextField(
                        value = newProductDescription,
                        onValueChange = { newProductDescription = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        decorationBox = { innerTextField ->
                            if (newProductDescription.isEmpty()) {
                                Text("Descrição do Produto")
                            }
                            innerTextField()
                        }
                    )

                    BasicTextField(
                        value = newProductQuantity,
                        onValueChange = { newProductQuantity = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        decorationBox = { innerTextField ->
                            if (newProductQuantity.isEmpty()) {
                                Text("Quantidade do Produto")
                            }
                            innerTextField()
                        }
                    )

                    Button(onClick = { /*Adicionar Logica para add producto*/ }) {
                        Text("Adicionar Produto")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    products.forEach { product ->
                        Text(product.name)
                        Text(product.description)
                        Text("R$ ${product.price}")
                        Text(product.supermarket)
                    }
                }
            }

            is ListsViewModel.ListState.Error -> {
                Text("Error: ${(state as ListsViewModel.ListState.Error).message}")
            }

            else -> {}

        }
    }
}