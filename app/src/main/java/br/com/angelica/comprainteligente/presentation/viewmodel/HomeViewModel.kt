package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.AddProductUseCase
import br.com.angelica.comprainteligente.domain.GetProductsUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Idle)
    val state: StateFlow<HomeState> = _state

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadProducts -> loadProducts()
            is HomeIntent.LoadFeaturedProducts -> loadFeaturedProducts()
            is HomeIntent.AddProduct -> addProduct(intent.product)
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            val result = getProductsUseCase()
            _state.value = if (result.isSuccess) {
                HomeState.ProductsLoaded(result.getOrNull()!!)
            } else {
                HomeState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            val result = getProductsUseCase()
            _state.value = if (result.isSuccess) {
                HomeState.FeatureProductsLoaded(result.getOrNull()!!)
            } else {
                HomeState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    private fun addProduct(product: Product) {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            val result = addProductUseCase(product)
            _state.value = if (result.isSuccess) {
                HomeState.ProductAdded(product)
            } else {
                HomeState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    sealed class HomeIntent {
        object LoadProducts: HomeIntent()
        object LoadFeaturedProducts: HomeIntent()
        data class AddProduct(val product: Product) : HomeIntent()
    }

    sealed class HomeState {
        object Idle : HomeState()
        object Loading : HomeState()
        data class ProductsLoaded(val products: List<Product>) : HomeState()
        data class FeatureProductsLoaded(val products: List<Product>) : HomeState()
        data class ProductAdded(val product: Product) : HomeState()
        data class Error(val message: String) : HomeState()
    }
}