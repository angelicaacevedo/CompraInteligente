package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.GetProductsUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ListsViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ListState>(ListState.Loading)
    val state: MutableStateFlow<ListState> = _state

    fun loadProducts() {
        viewModelScope.launch {
            val result = getProductsUseCase()
            _state.value = if (result.isSuccess) {
                ListState.Success(result.getOrNull() ?: emptyList())
            } else {
                ListState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    sealed class ListState {
        object Loading : ListState()
        data class Success(val products: List<Product>) : ListState()
        data class ProductsLoaded(val products: List<Product>) : ListState()
        data class Error(val message: String) : ListState()

    }
}