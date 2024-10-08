package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val productUseCase: ProductUseCase
) : ViewModel() {
    fun addProductToFirestore(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = productUseCase.addProduct(product)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }
}