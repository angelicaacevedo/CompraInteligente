package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.GetProductDetailUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProductDetailState>(ProductDetailState.Loading)
    val state: MutableStateFlow<ProductDetailState> = _state

    fun loadProductDetails(productId: String) {
        viewModelScope.launch {
            val result = getProductDetailUseCase(productId)
            _state.value = if (result.isSuccess) {
                ProductDetailState.Success(result.getOrNull() ?: Product())
            } else {
                ProductDetailState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }


    sealed class ProductDetailState {
        object Loading : ProductDetailState()
        data class Success(val product: Product) : ProductDetailState()
        data class Error(val message: String) : ProductDetailState()
    }

}