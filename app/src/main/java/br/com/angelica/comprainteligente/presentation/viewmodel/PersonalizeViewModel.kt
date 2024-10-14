package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.data.category.CategoryRepository
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonalizeViewModel(
    private val productUseCase: ProductUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _favoriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favoriteProducts: StateFlow<List<Product>> = _favoriteProducts

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadCategories()
        loadFavoriteProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            if (result.isSuccess) {
                _categories.value = result.getOrDefault(emptyList())
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    private fun loadFavoriteProducts() {
        viewModelScope.launch {
            val result = productUseCase.getProducts()
            if (result.isSuccess) {
                _favoriteProducts.value = result.getOrDefault(emptyList()).filter { it.isFavorite }
            }
        }
    }

    fun addCategory(category: String) {
        viewModelScope.launch {
            val result = categoryRepository.addCategory(category)
            if (result.isSuccess) {
                loadCategories()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun removeCategory(category: String) {
        viewModelScope.launch {
            val result = categoryRepository.removeCategory(category)
            if (result.isSuccess) {
                loadCategories()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }
}