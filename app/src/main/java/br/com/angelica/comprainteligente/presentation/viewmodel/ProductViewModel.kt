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
                intent.barcode,
                intent.name,
                intent.price,
                intent.supermarket
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

    private fun registerProduct(barcode: String, name: String, price: String, supermarket: String) {
        viewModelScope.launch {
            _state.value = ProductState.Loading

            // Criar o objeto do produto com as informações adicionais
            val product = Product(
                id = barcode,
                name = name,
                categoryId = "",    // Pode ser ajustado para adicionar uma categoria real
                imageUrl = ""       // Pode ser ajustado para adicionar uma URL real de imagem
            )

            // Criar o objeto de preço
            val productPrice = Price(
                productId = barcode,
                supermarketId = supermarket,
                price = price.toDouble(),
                date = Timestamp.now()
            )

            val result = registerProductUseCase.execute(product, productPrice)

            if (result.isSuccess) {
                _state.value = ProductState.ProductRegistered
            } else {
                _state.value = ProductState.Error("Erro ao registrar o produto.")
            }
        }
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
            val supermarket: String
        ) : ProductIntent()

        data class LoadSuggestions(val query: String) : ProductIntent()
    }
}
