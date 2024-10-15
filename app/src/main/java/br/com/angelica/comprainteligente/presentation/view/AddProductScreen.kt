package br.com.angelica.comprainteligente.presentation.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.AddProductViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: AddProductViewModel = getViewModel(),
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()
    val scrollState = rememberScrollState()

    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var isPriceError by remember { mutableStateOf(false) }

    // Chama fetchCategories quando a tela for exibida
    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Produtos") },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White
                ),
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Nome do Produto",
                style = MaterialTheme.typography.titleMedium,
            )
            CustomTextField(
                value = productName,
                onValueChange = { productName = it },
                label = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Preço",
                style = MaterialTheme.typography.titleMedium,
            )
            CustomTextField(
                value = productPrice,
                onValueChange = {
                    productPrice = it
                    isPriceError = false
                },
                label = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                isError = isPriceError,
                errorMessage = "Preço inválido",
                isNumeric = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Categoria",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            CategoryDropdownMenu(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                categories = categories,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClickableButton(
                    onClick = {
                        val price = productPrice.replace(",", ".").toDoubleOrNull()
                        if (productName.isNotEmpty() && price != null && selectedCategory.isNotEmpty()) {
                            val product = Product(
                                name = productName,
                                price = price,
                                category = selectedCategory
                            )
                            viewModel.addProductToFirestore(product,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Produto adicionado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { error ->
                                    Toast.makeText(
                                        context,
                                        "Erro ao adicionar: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        } else {
                            if (price == null) {
                                isPriceError = true
                            }
                            Toast.makeText(
                                context,
                                "Por favor, preencha todos os campos corretamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    text = "Adicionar ao Carrinho",
                    icon = Icons.Filled.AddShoppingCart
                )

                Spacer(modifier = Modifier.width(8.dp))

                ClickableButton(
                    onClick = {
                        // Ação ao clicar no botão de favorito
                    },
                    text = "Favorito",
                    icon = Icons.Outlined.Star
                )
            }
        }
    }
}

@Composable
fun ClickableButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        modifier = Modifier
            .wrapContentSize()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (isPressed) Color.Gray else Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = if (isPressed) Color.Gray else Color.White)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownMenu(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = if (selectedCategory.isEmpty()) "Selecione a Categoria" else selectedCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.Transparent)
                    .width(IntrinsicSize.Max)
                    .zIndex(1f)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
