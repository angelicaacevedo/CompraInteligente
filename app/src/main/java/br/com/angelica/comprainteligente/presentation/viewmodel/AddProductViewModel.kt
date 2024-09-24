package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.AddProductUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddProductViewModel(private val addProductUseCase: AddProductUseCase) : ViewModel() {
    private val _state = MutableStateFlow<AddProductState>(AddProductState.Idle)
    val state: MutableStateFlow<AddProductState> = _state

    fun addProduct(product: Product) {
        viewModelScope.launch {
            val result = addProductUseCase(product)
            _state.value = if (result.isSuccess) {
                AddProductState.Success
            } else {
                AddProductState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }


    sealed class AddProductState {
        object Idle : AddProductState()
        object Success : AddProductState()
        data class Error(val message: String) : AddProductState()
    }
}