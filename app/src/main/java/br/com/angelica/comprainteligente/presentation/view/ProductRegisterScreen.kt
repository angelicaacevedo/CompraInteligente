package br.com.angelica.comprainteligente.presentation.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Category
import br.com.angelica.comprainteligente.presentation.common.CustomAlertDialog
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.CustomTextField
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductViewModel
import br.com.angelica.comprainteligente.theme.BackgroundLightGray
import br.com.angelica.comprainteligente.theme.BlueSoft
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.TextAccent
import br.com.angelica.comprainteligente.theme.TextBlack
import br.com.angelica.comprainteligente.theme.White
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductRegisterScreen(
    userId: String,
    onProductRegistered: () -> Unit,
    navController: NavController,
    viewModel: ProductViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf(false) }
    var priceErrorMessage by remember { mutableStateOf("") }

    // Estados dos campos de entrada
    var productName by remember { mutableStateOf("") }
    var productImageUrl by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var selectedSupermarket by remember { mutableStateOf("") }
    var selectedPlaceId by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    var isFormSubmitted by remember { mutableStateOf(false) }
    var isBarcodeEditable by remember { mutableStateOf(true) }
    var isProductInfoEditable by remember { mutableStateOf(true) }
    var isSupermarketEditable by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var isSearchCompleted by remember { mutableStateOf(false) }
    var isManualEntry by remember { mutableStateOf(false) }

    val context = LocalContext.current as Activity

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Criação do launcher para abrir o scanner de código de barras
    val barcodeScannerLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            barcode = result.contents
            isBarcodeEditable = false  // Bloqueia o campo de código de barras após escanear
            isProductInfoEditable = false
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

    LaunchedEffect(suggestions.size) {
        if (suggestions.isNotEmpty()) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(suggestions.size - 1)
            }
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
                isProductInfoEditable = false
            }

            is ProductViewModel.ProductState.SuggestionsLoaded -> {
                suggestions = (state as ProductViewModel.ProductState.SuggestionsLoaded).suggestions
                isLoading = false
            }

            is ProductViewModel.ProductState.ProductRegistered -> {
                showSuccessDialog = true
                isLoading = false
            }

            is ProductViewModel.ProductState.Error -> {
                errorMessage = (state as ProductViewModel.ProductState.Error).message
                showErrorDialog = true
                isLoading = false
            }

            else -> Unit
        }
    }

    if (showSuccessDialog) {
        CustomAlertDialog(
            title = "Produto Cadastrado",
            message = "Produto cadastrado com sucesso! Deseja cadastrar mais um?",
            confirmButtonText = "Sim",
            dismissButtonText = "Não",
            onConfirm = {
                showSuccessDialog = false
                productName = ""
                productImageUrl = ""
                productPrice = ""
                barcode = ""
                selectedCategory = null
                selectedSupermarket = ""
                isFormSubmitted = false
                isBarcodeEditable = true
                isSupermarketEditable = true
                viewModel.resetState()
            },
            onDismiss = {
                showSuccessDialog = false
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
                isLoading = false
                productName = ""
                productImageUrl = ""
                productPrice = ""
                barcode = ""
                selectedSupermarket = ""
                isFormSubmitted = false
                isBarcodeEditable = true
                isSupermarketEditable = true
                viewModel.resetState()
            },
            onDismiss = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cadastro de Produto", modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController = navController, userId = userId)
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp)
            ) {
                // Alternar entre cadastro com código de barras ou manual
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isManualEntry) "Cadastro Manual" else "Código de Barras",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Switch(
                                checked = isManualEntry,
                                onCheckedChange = { isManualEntry = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PrimaryBlue, // Cor do botão circular quando ligado
                                    checkedTrackColor = BackgroundLightGray, // Cor da trilha quando ligado
                                    uncheckedThumbColor = TextAccent, // Cor do botão circular quando desligado
                                    uncheckedTrackColor = BackgroundLightGray, // Cor da trilha quando desligado
                                ),
                                modifier = Modifier.padding(8.dp) // Adiciona espaçamento ao redor
                            )
                        }

                        // Texto explicativo abaixo do switch
                        Text(
                            text = if (isManualEntry)
                                "Desativei o cadastro manual. Insira os dados via código de barras."
                            else
                                "Ativei o cadastro manual. Preencha os campos manualmente.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Campo para nome do produto (apenas para cadastro manual)
                if (isManualEntry) {
                    item {
                        CustomTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = "Nome do Produto",
                            isError = isFormSubmitted && productName.isEmpty(),
                            errorMessage = "Campo obrigatório"
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    // Campo para código de barras (somente para cadastro com scanner ou manual por código)
                    item {
                        CustomTextField(
                            value = barcode,
                            onValueChange = { barcode = it },
                            label = "Código de Barras",
                            isError = isFormSubmitted && barcode.isEmpty(),
                            errorMessage = "Campo obrigatório",
                            enabled = isBarcodeEditable,
                            isNumeric = true,
                            onFocusChanged = { focusState ->
                                if (!focusState.isFocused && barcode.isNotEmpty()) {
                                    isBarcodeEditable = false
                                    isLoading = true
                                    viewModel.handleIntent(
                                        ProductViewModel.ProductIntent.ScanProduct(barcode)
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Search,
                                    contentDescription = "Buscar Código"
                                )
                            }
                        )
                    }
                }

                if (isManualEntry) {
                    // DropdownMenu para selecionar unidade de medida
                    item {
                        var selectedUnit by remember { mutableStateOf("Unidade") }
                        var isDropdownExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedUnit,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                listOf("Selecione a Unidade", "Kg", "Gr", "Litro").forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            selectedUnit = unit
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Nome e Imagem do Produto
                    if (productName.isNotEmpty()) {
                        item {
                            CustomTextField(
                                value = productName,
                                onValueChange = { productName = it },
                                label = "Nome do Produto",
                                isError = isFormSubmitted && productName.isEmpty(),
                                errorMessage = "Campo obrigatório",
                                enabled = isProductInfoEditable
                            )
                        }
                    }

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
                }

                item {
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                barcodeScannerLauncher.launch(ScanOptions().apply {
                                    setPrompt("Posicione o código de barras dentro do quadro.")
                                    setCameraId(0)
                                    setBeepEnabled(true)
                                })
                            } else {
                                // Solicitar permissão de câmera
                                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Escanear Código de Barras",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Campo para inserir o preço do produto
                item {
                    CustomTextField(
                        value = productPrice,
                        onValueChange = {
                            productPrice = it
                            val priceValue = it.replace(",", ".").toDoubleOrNull()
                            if (priceValue == null || priceValue <= 0) {
                                priceError = true
                                priceErrorMessage = "O preço não pode ser zero ou negativo"
                            } else {
                                priceError = false
                                priceErrorMessage = ""
                            }
                        },
                        label = "Preço",
                        isNumeric = true,
                        isError = isFormSubmitted && (productPrice.isEmpty() || priceError),
                        errorMessage = if (priceError) priceErrorMessage else "Campo obrigatório",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = isCategoryMenuExpanded,
                        onExpandedChange = { isCategoryMenuExpanded = !isCategoryMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Selecione uma categoria",
                            onValueChange = {},
                            label = { Text("Categoria") },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { isCategoryMenuExpanded = true },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = isCategoryMenuExpanded,
                            onDismissRequest = { isCategoryMenuExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(text = category.name) },
                                    onClick = {
                                        selectedCategory = category
                                        isCategoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Campo para selecionar supermercado
                item {
                    CustomTextField(
                        value = selectedSupermarket,
                        onValueChange = {
                            if (isSupermarketEditable) {
                                selectedSupermarket = it
                                if (selectedSupermarket.isNotEmpty()) {
                                    isSearchCompleted = false
                                    viewModel.handleIntent(
                                        ProductViewModel.ProductIntent.LoadSuggestions(it)
                                    )
                                } else {
                                    suggestions = emptyList()
                                }
                            }
                        },
                        label = "Supermercado",
                        isError = isFormSubmitted && selectedSupermarket.isEmpty(),
                        errorMessage = "Campo obrigatório",
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = "Buscar Supermercado"
                            )
                        },
                        onFocusChanged = { focusState ->
                            if (!focusState.isFocused) {
                                isSearchCompleted = true
                                suggestions = emptyList()
                            }
                        }
                    )
                }

                // Sugestões de Supermercados
                if (suggestions.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = BlueSoft),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                suggestions.forEachIndexed { index, (name, placeId) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedSupermarket = name
                                                selectedPlaceId = placeId
                                                suggestions = emptyList()
                                                isSupermarketEditable = false
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextBlack
                                            )
                                        )
                                    }
                                    if (index < suggestions.size - 1) {
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.1f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Botão para registrar o produto
                item {
                    Button(
                        onClick = {
                            isFormSubmitted = true
                            val priceValue = productPrice.replace(",", ".").toDoubleOrNull()

                            // Validação completa
                            if (((isManualEntry && productName.isNotEmpty()) || (!isManualEntry && barcode.isNotEmpty())) &&
                                selectedSupermarket.isNotEmpty() && priceValue != null && priceValue > 0
                            ) {
                                viewModel.handleIntent(
                                    ProductViewModel.ProductIntent.RegisterProduct(
                                        barcode = barcode,
                                        name = productName,
                                        price = productPrice,
                                        supermarket = selectedSupermarket,
                                        userId = userId,
                                        placeId = selectedPlaceId,
                                        isManual = isManualEntry
                                    )
                                )
                            } else {
                                // Exibe mensagens de erro nos campos com problema
                                if (selectedSupermarket.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Por favor, selecione um supermercado.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                if (priceValue == null || priceValue <= 0) {
                                    priceError = true
                                    priceErrorMessage = "O preço não pode ser zero ou negativo"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Cadastrar Produto",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Exibe o indicador de carregamento cobrindo toda a tela
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { /* Bloqueia cliques enquanto carrega */ },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

