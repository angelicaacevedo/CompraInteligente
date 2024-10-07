package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddProductViewModel : ViewModel() {
    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> get() = _productList

    init {
        _productList.value = mutableListOf()
    }

    fun addProduct(product: Product) {
        val currentList = _productList.value?.toMutableList() ?: mutableListOf()
        currentList.add(product)
        _productList.value = currentList
    }
}