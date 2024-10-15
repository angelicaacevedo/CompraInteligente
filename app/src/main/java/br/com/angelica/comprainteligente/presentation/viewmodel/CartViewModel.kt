package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.launch

class CartViewModel(
    private val productUseCase: ProductUseCase
) : ViewModel() {
    private val _state = mutableStateOf<CartState>(CartState.Loading)
    val state: State<CartState> = _state

    init {
        handleIntent(CartIntent.LoadCart)
    }

    fun handleIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.LoadCart -> fetchCartProducts()
            is CartIntent.RemoveProduct -> removeProductFromCart(intent.productId)
            is CartIntent.ChangeProductFavoriteStatus -> changeFavoriteStatus(intent.productId, intent.isFavorite)
        }
    }

    private fun fetchCartProducts() {
        viewModelScope.launch {
            _state.value = CartState.Loading
            val result = productUseCase.getProducts()
            _state.value = result.fold(
                onSuccess = { CartState.Success(it) },
                onFailure = { CartState.Error(it.message ?: "Unknown Error") }
            )
        }
    }

    private fun removeProductFromCart(productId: String) {
        viewModelScope.launch {
            productUseCase.removeProduct(productId)
            fetchCartProducts()
        }
    }

    private fun changeFavoriteStatus(productId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            productUseCase.changeProductFavoriteStatus(productId, isFavorite)
            fetchCartProducts()
        }
    }

    sealed class CartState {
        object Loading : CartState()
        data class Success(val products: List<Product>) : CartState()
        data class Error(val message: String) : CartState()
    }

    sealed class CartIntent {
        object LoadCart : CartIntent()
        data class RemoveProduct(val productId: String) : CartIntent()
        data class ChangeProductFavoriteStatus(val productId: String, val isFavorite: Boolean) : CartIntent()
    }
}