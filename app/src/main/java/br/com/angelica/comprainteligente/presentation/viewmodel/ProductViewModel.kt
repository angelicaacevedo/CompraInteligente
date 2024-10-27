package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.GetProductInfoFromBarcodeUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetSupermarketSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.RegisterProductUseCase
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
    private val registerProductUseCase: RegisterProductUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProductState>(ProductState.Idle)
    val state: StateFlow<ProductState> = _state

    fun handleIntent(intent: ProductIntent) {
        when (intent) {
            is ProductIntent.ScanProduct -> scanProduct(intent.barcode)
            is ProductIntent.RegisterProduct -> registerProduct(
                barcode = intent.barcode,
                name = intent.name,
                price = intent.price,
                supermarket = intent.supermarket,
                userId = intent.userId
            )

            is ProductIntent.LoadSuggestions -> loadSuggestions(intent.query)
        }
    }

    private fun scanProduct(barcode: String) {
        viewModelScope.launch {
            _state.value = ProductState.Loading
            val result = getProductInfoFromBarcodeUseCase.execute(barcode)
            if (result.isSuccess) {
                val productDetails = result.getOrNull()
                _state.value = ProductState.ProductScanned(productDetails)
            } else {
                _state.value = ProductState.Error("Produto não encontrado.")
            }
        }
    }

    private fun loadSuggestions(query: String) {
        viewModelScope.launch {
            _state.value = ProductState.Loading
            val suggestions = getSupermarketSuggestionsUseCase.execute(query)
            _state.value = if (suggestions.isNotEmpty()) {
                ProductState.SuggestionsLoaded(suggestions)
            } else {
                ProductState.Error("Nenhum supermercado encontrado")
            }
        }
    }

    private fun registerProduct(
        barcode: String,
        name: String,
        price: String,
        supermarket: String,
        userId: String
    ) {
        viewModelScope.launch {
            _state.value = ProductState.Loading

            // Busca detalhes do produto, incluindo a URL da imagem
            val productDetailsResult = getProductInfoFromBarcodeUseCase.execute(barcode)
            val imageUrl = if (productDetailsResult.isSuccess) {
                productDetailsResult.getOrNull()?.image_url ?: ""
            } else ""

            // Criando o objeto Product e Price com as informações fornecidas
            val product = Product(
                id = barcode,
                name = name,
                categoryId = "",    // Pode ser ajustado para adicionar uma categoria real
                imageUrl = imageUrl,
                userId = userId
            )

            val productPrice = Price(
                productId = barcode,
                supermarketId = supermarket,
                price = price.replace(",", ".").toDouble(),
                date = Timestamp.now(),
                userId = userId
            )

            // Executando o caso de uso para registrar o produto e o preço
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
