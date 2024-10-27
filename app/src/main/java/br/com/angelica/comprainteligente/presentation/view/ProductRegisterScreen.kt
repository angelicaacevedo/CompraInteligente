package br.com.angelica.comprainteligente.presentation.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.presentation.common.CustomAlertDialog
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductRegisterScreen(
    userId: String,
    onProductRegistered: () -> Unit,
    navController: NavController,
    viewModel: ProductViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showSucessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Estados dos campoas de entrada
    var productName by remember { mutableStateOf("") }
    var productImageUrl by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var selectedSupermarket by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var isFormSubmitted by remember { mutableStateOf(false) }
    var isBarcodeEditable by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current as Activity

    // Criação do launcher para abrir o scanner de código de barras
    val barcodeScannerLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            barcode = result.contents
            isBarcodeEditable = false  // Bloqueia o campo de código de barras após escanear
            isLoading = true
            viewModel.handleIntent(ProductViewModel.ProductIntent.ScanProduct(barcode))
        } else {
            Toast.makeText(context, "Escaneamento cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    // Criação do launcher para solicitar permissão de câmera
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permissão concedida, iniciar o escaneamento do código de barras
            val options = ScanOptions()
            options.setPrompt("Posicione o código de barras dentro do quadro.")
            options.setCameraId(0)  // Usar a câmera traseira
            options.setBeepEnabled(true)  // Habilitar som ao escanear
            barcodeScannerLauncher.launch(options)
        } else {
            // Permissão negada, exibir mensagem de erro
            Toast.makeText(
                context,
                "Permissão da câmera é necessária para escanear o código de barras",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is ProductViewModel.ProductState.ProductScanned -> {
                val productDetails =
                    (state as ProductViewModel.ProductState.ProductScanned).productDetails
                productName = productDetails?.product_name ?: "Produto não encontrado"
                productImageUrl = productDetails?.image_url ?: ""
                isLoading = false
            }

            is ProductViewModel.ProductState.SuggestionsLoaded -> {
                suggestions =
                    (state as ProductViewModel.ProductState.SuggestionsLoaded).suggestions.map { suggestion ->
                        val parts = suggestion.split(",")
                        parts[0] to parts.getOrElse(1) { "" }
                    }
                isLoading = false
            }

            is ProductViewModel.ProductState.ProductRegistered -> showSucessDialog = true

            is ProductViewModel.ProductState.Error -> {
                errorMessage = (state as ProductViewModel.ProductState.Error).message
                showErrorDialog = true
            }
            else -> Unit
        }
    }

    if (showSucessDialog) {
        CustomAlertDialog(
            title = "Produto Cadastrado",
            message = "Produto cadastrado com sucesso! Deseja cadastrar mais um?",
            confirmButtonText = "Sim",
            dismissButtonText = "Não",
            onConfirm = {
                showSucessDialog = false
                productName = ""
                productImageUrl = ""
                productPrice = ""
                barcode = ""
                selectedSupermarket = ""
                isFormSubmitted = false
                isBarcodeEditable = true
                viewModel.resetState()
            },
            onDismiss = {
                showSucessDialog = false
                onProductRegistered()
            }
        )
    }

    if (showErrorDialog) {
        CustomAlertDialog(
            title = "Erro",
            message = errorMessage,
            confirmButtonText = "Ok",
            onConfirm = {
                showErrorDialog = false
                productName = ""
                productImageUrl = ""
                productPrice = ""
                barcode = ""
                selectedSupermarket = ""
                isFormSubmitted = false
                isBarcodeEditable = true
                viewModel.resetState()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Cadastro de Produto", modifier = Modifier.fillMaxWidth()) },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.Black,
                    containerColor = Color.White,
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController = navController, userId = userId)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                // Campo para inserir ou escanear o código de barras
                CustomTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = "Código de Barras",
                    isError = isFormSubmitted && barcode.isEmpty(),
                    errorMessage = "Campo obrigatório",
                    enabled = isBarcodeEditable,  // Controla se o campo pode ser editado
                    isNumeric = true,
                    onFocusChanged = { focusState ->
                        if (!focusState.isFocused && barcode.isNotEmpty()) {
                            isLoading = true
                            viewModel.handleIntent(
                                ProductViewModel.ProductIntent.ScanProduct(
                                    barcode
                                )
                            )
                        }
                    }
                )
            }

            item {
                Button(
                    onClick = {
                        // Verificar se a permissão de câmera já foi concedida
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // Permissão já foi concedida, iniciar escaneamento
                            val options = ScanOptions()
                            options.setPrompt("Posicione o código de barras dentro do quadro.")
                            options.setCameraId(0)  // Usar a câmera traseira
                            options.setBeepEnabled(true)  // Habilitar som ao escanear
                            barcodeScannerLauncher.launch(options)
                        } else {
                            // Solicitar permissão de câmera
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Escanear Código de Barras")
                }
            }

            // Exibir nome e descrição do produto obtido da API
            if (productName.isNotEmpty()) {
                item {
                    CustomTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = "Nome do Produto",
                        isError = isFormSubmitted && barcode.isEmpty(),
                        errorMessage = "Campo obrigatório",
                        enabled = isBarcodeEditable  // Controla se o campo pode ser editado
                    )
                }
            }

            // Exibir imagem do produto (se disponível)
            if (productImageUrl.isNotEmpty()) {
                item {
                    Image(
                        painter = rememberImagePainter(productImageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(vertical = 8.dp)
                    )
                }
            }

            item {
                // Campo para inserir o preço do produto
                CustomTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    label = "Preço",
                    isNumeric = true,
                    isError = isFormSubmitted && productPrice.isEmpty(),
                    errorMessage = "Campo obrigatório"
                )
            }

            item {
                // Campo para selecionar supermercado
                CustomTextField(
                    value = selectedSupermarket,
                    onValueChange = {
                        selectedSupermarket = it
                        viewModel.handleIntent(ProductViewModel.ProductIntent.LoadSuggestions(it))
                    },
                    label = "Supermercado",
                    isError = isFormSubmitted && selectedSupermarket.isEmpty(),
                    errorMessage = "Campo obrigatório"
                )
            }

            // Exibe as sugestões de supermercados
            if (suggestions.isNotEmpty()) {
                items(suggestions) { (name, address) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedSupermarket = name
                                suggestions = emptyList()  // Limpa as sugestões após selecionar
                            }
                            .padding(8.dp)
                    ) {
                        Text(text = name, color = Color.Black)  // Nome do supermercado
                        Text(
                            text = address,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )  // Endereço do supermercado
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Botão para registrar o produto
                Button(
                    onClick = {
                        // Define que o formulário foi submetido
                        isFormSubmitted = true

                        // Verifica se todos os campos obrigatórios foram preenchidos
                        if (barcode.isNotEmpty() && productPrice.isNotEmpty() && selectedSupermarket.isNotEmpty()) {
                            viewModel.handleIntent(
                                ProductViewModel.ProductIntent.RegisterProduct(
                                    barcode = barcode,
                                    name = productName,
                                    price = productPrice,
                                    supermarket = selectedSupermarket,
                                    userId = userId
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cadastrar Produto")
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()  // Preenche o espaço total da tela
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)  // Centraliza dentro do Box
                )
            }
        }
    }
}
