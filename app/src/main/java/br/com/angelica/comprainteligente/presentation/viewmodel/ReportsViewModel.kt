package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val productUseCase: ProductUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ReportsState>(ReportsState.Loading)
    val state: StateFlow<ReportsState> = _state

    fun loadPriceHistory(productName: String) {
        viewModelScope.launch {
            _state.value = ReportsState.Loading
            val result = productUseCase.getPriceHistory(productName)
            _state.value = if (result.isSuccess) {
                ReportsState.Success(result.getOrNull()!!)
            } else {
                ReportsState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    sealed class ReportsState {
        object Loading : ReportsState()
        data class Success(val priceHistory: List<Product>) : ReportsState()
        data class Error(val message: String) : ReportsState()
    }
}