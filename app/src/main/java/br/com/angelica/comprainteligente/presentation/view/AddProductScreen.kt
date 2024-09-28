package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.viewmodel.AddProductViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun AddProductScreen(viewModel: AddProductViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var supermarket by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (name.isEmpty()) {
                    Text("Nome do Produto")
                }
                innerTextField()
            }
        )

        BasicTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (description.isEmpty()) {
                    Text("Descrição do Produto")
                }
                innerTextField()
            }
        )

        BasicTextField(
            value = price,
            onValueChange = { price = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (price.isEmpty()) {
                    Text("Preço")
                }
                innerTextField()
            }
        )

        BasicTextField(
            value = supermarket,
            onValueChange = { supermarket = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (supermarket.isEmpty()) {
                    Text("Supermercado")
                }
                innerTextField()
            }
        )

        Button(onClick = {
            val product = Product(name, description, price.toDouble(), supermarket)
            viewModel.addProduct(product)
        }) {
            Text("Adicionar Produto")
        }

        when (state) {
            is AddProductViewModel.AddProductState.Success -> {
                Text("Produto adicionado com sucesso!")
            }

            is AddProductViewModel.AddProductState.Error -> {
                Text("Erro ao adicionar produto: ${(state as AddProductViewModel.AddProductState.Error).message}")
            }

            else -> {}
        }
    }
}
