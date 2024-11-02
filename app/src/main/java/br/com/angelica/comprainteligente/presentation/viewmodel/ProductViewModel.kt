package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.GetCategoriesUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetProductInfoFromBarcodeUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetSupermarketSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.RegisterProductUseCase
import br.com.angelica.comprainteligente.model.Category
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.remote.ProductDetails
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val getProductInfoFromBarcodeUseCase: GetProductInfoFromBarcodeUseCase,
    private val getSupermarketSuggestionsUseCase: GetSupermarketSuggestionsUseCase,
    private val registerProductUseCase: RegisterProductUseCase,
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
            val result = getProductInfoFromBarcodeUseCase.execute(barcode)
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
            val productPrice = createProductPrice(intent)

            registerProduct(product, productPrice)
        }
    }

    private suspend fun createProduct(intent: ProductIntent.RegisterProduct): Product {
        val productDetailsResult = getProductInfoFromBarcodeUseCase.execute(intent.barcode)
        val imageUrl = productDetailsResult.getOrNull()?.image_url ?: ""

        return Product(
            id = intent.barcode,
            name = intent.name,
            categoryId = "", // Pode ser ajustado para adicionar uma categoria real
            imageUrl = imageUrl,
            userId = intent.userId
        )
    }

    private fun createProductPrice(intent: ProductIntent.RegisterProduct): Price {
        val priceValue = intent.price.replace(",", ".").toDouble()
        return Price(
            productId = intent.barcode,
            supermarketId = intent.supermarket,
            price = priceValue,
            date = Timestamp.now(),
            userId = intent.userId
        )
    }

    private fun registerProduct(product: Product, productPrice: Price) {
        viewModelScope.launch {
            val result = registerProductUseCase.execute(product, productPrice)
            _state.value = if (result.isSuccess) {
                ProductState.ProductRegistered
            } else {
                val errorMessage = result.exceptionOrNull()?.message
                if (errorMessage == "Esse produto com o mesmo supermercado e preço já está cadastrado.") {
                    ProductState.Error("Produto já cadastrado com este preço e supermercado.")
                } else {
                    ProductState.Error("Erro ao registrar o produto.")
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
        data class SuggestionsLoaded(val suggestions: List<String>) : ProductState()
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
            val userId: String
        ) : ProductIntent()

        data class LoadSuggestions(val query: String) : ProductIntent()
    }
}
