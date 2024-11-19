package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.GetCategoriesUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetSupermarketSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.ProductOperationsUseCase
import br.com.angelica.comprainteligente.model.Category
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.remote.ProductDetails
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProductViewModel(
    private val getSupermarketSuggestionsUseCase: GetSupermarketSuggestionsUseCase,
    private val productOperationsUseCase: ProductOperationsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProductState>(ProductState.Idle)
    val state: StateFlow<ProductState> = _state

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private var isSearchCompleted = false

    init {
        loadCategories()
    }

    fun handleIntent(intent: ProductIntent) {
        when (intent) {
            is ProductIntent.ScanProduct -> scanProduct(intent.barcode)
            is ProductIntent.RegisterProduct -> processProductRegistration(intent)
            is ProductIntent.LoadSuggestions -> loadSuggestions(intent.query)
        }
    }

    private fun loadCategories() {
        _categories.value = getCategoriesUseCase.execute()
    }

    private fun scanProduct(barcode: String) {
        viewModelScope.launch {
            _state.value = ProductState.Loading
            val result = productOperationsUseCase.getProductInfoFromBarcode(barcode)
            _state.value = if (result.isSuccess) {
                ProductState.ProductScanned(result.getOrNull())
            } else {
                ProductState.Error("Produto não encontrado.")
            }
        }
    }

    private fun loadSuggestions(query: String) {
        viewModelScope.launch {
            _state.value = ProductState.Loading
            val suggestions = getSupermarketSuggestionsUseCase.execute(query)
            _state.value = if (suggestions.isNotEmpty()) {
                isSearchCompleted = false
                ProductState.SuggestionsLoaded(suggestions)
            } else if (isSearchCompleted) {
                ProductState.Error("Nenhum supermercado encontrado")
            } else {
                ProductState.Idle
            }
        }
    }

    private fun processProductRegistration(intent: ProductIntent.RegisterProduct) {
        viewModelScope.launch {
            _state.value = ProductState.Loading

            if (!isPriceValid(intent.price)) {
                _state.value = ProductState.Error("O preço deve ser maior que zero.")
                return@launch
            }

            val product = createProduct(intent)
            val productPrice = createProductPrice(intent, product.id)

            // Registra o produto e o preço
            val result =
                productOperationsUseCase.registerProduct(product, productPrice, intent.placeId)
            _state.value = if (result.isSuccess) {
                ProductState.ProductRegistered
            } else {
                ProductState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    private suspend fun createProduct(intent: ProductIntent.RegisterProduct): Product {
        val productId = if (intent.isManual) {
            UUID.randomUUID().toString() // Gera ID para produtos manuais
        } else {
            intent.barcode
        }

        // Para produtos automáticos, busca informações do OpenFoodFacts
        val productDetailsResult =
            if (!intent.isManual) productOperationsUseCase.getProductInfoFromBarcode(productId) else null
        val imageUrl = productDetailsResult?.getOrNull()?.image_url ?: ""

        return Product(
            id = productId,
            name = intent.name,
            imageUrl = imageUrl,
            userId = intent.userId,
            isManual = intent.isManual
        )
    }

    private fun createProductPrice(
        intent: ProductIntent.RegisterProduct,
        productId: String
    ): Price {
        val priceValue = intent.price.replace(",", ".").toDouble()
        return Price(
            productId = productId, // Garante que o ID do produto está associado corretamente
            supermarketId = intent.supermarket,
            price = priceValue,
            date = Timestamp.now(),
            userId = intent.userId
        )
    }

    private fun registerProduct(product: Product, productPrice: Price, placeId: String) {
        viewModelScope.launch {
            if (placeId.isEmpty()) {
                _state.value =
                    ProductState.Error("Erro: Place ID do supermercado é inválido ou vazio.")
                return@launch
            }

            val result = productOperationsUseCase.registerProduct(product, productPrice, placeId)
            _state.value = if (result.isSuccess) {
                val registeredProduct = result.getOrNull()
                if (registeredProduct != null) {
                    productPrice.productId = registeredProduct.id
                    val priceResult = productOperationsUseCase.registerPrice(productPrice)
                    if (priceResult.isSuccess) {
                        ProductState.ProductRegistered
                    } else {
                        ProductState.Error("Erro ao registrar o preço: ${priceResult.exceptionOrNull()?.message}")
                    }
                } else {
                    ProductState.Error("Erro ao obter produto registrado.")
                }
            } else {
                val errorMessage = result.exceptionOrNull()?.message
                if (errorMessage == "Esse produto com o mesmo supermercado e preço já está cadastrado.") {
                    ProductState.Error("Produto já cadastrado com este preço e supermercado.")
                } else {
                    ProductState.Error("Erro ao registrar o produto: $errorMessage")
                }
            }
        }
    }

    private fun isPriceValid(price: String): Boolean {
        val priceValue = price.replace(",", ".").toDoubleOrNull()
        return priceValue != null && priceValue > 0
    }

    fun resetState() {
        _state.value = ProductState.Idle
    }

    // Estados da ViewModel
    sealed class ProductState {
        object Idle : ProductState()
        object Loading : ProductState()
        object ProductRegistered : ProductState()
        data class ProductScanned(val productDetails: ProductDetails?) : ProductState()
        data class SuggestionsLoaded(val suggestions: List<Pair<String, String>>) :
            ProductState() // Nome e PlaceId

        data class Error(val message: String) : ProductState()
    }

    // Intenções da ViewModel
    sealed class ProductIntent {
        data class ScanProduct(val barcode: String) : ProductIntent()
        data class RegisterProduct(
            val barcode: String,
            val name: String,
            val price: String,
            val supermarket: String,
            val userId: String,
            val placeId: String,
            val isManual: Boolean
        ) : ProductIntent()

        data class LoadSuggestions(val query: String) : ProductIntent()
    }
}
