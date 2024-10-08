package br.com.angelica.comprainteligente.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.AddProductViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel = getViewModel(),
    onNavigateBack: () -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var isPriceError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Produto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = "Nome do Produto",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    label = "Descrição do Produto",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = productPrice,
                    onValueChange = {
                        productPrice = it
                        isPriceError = false
                    },
                    label = "Preço do Produto",
                    modifier = Modifier.fillMaxWidth(),
                    isError = isPriceError,
                    errorMessage = "Preço inválido",
                    isNumeric = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val price = productPrice.replace(",", ".").toDoubleOrNull()
                        if (productName.isNotEmpty() && price != null) {
                            val product = Product(
                                name = productName,
                                description = productDescription,
                                price = price
                            )
                            viewModel.addProductToFirestore(product,
                                onSuccess = {
                                    Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                },
                                onError = { error ->
                                    Toast.makeText(context, "Erro ao adicionar: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            if (price == null) {
                                isPriceError = true
                            }
                            Toast.makeText(context, "Por favor, preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar Produto", color = Color.White)
                }
            }
        }
    )
}
