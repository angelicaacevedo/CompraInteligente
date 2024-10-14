package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.data.category.CategoryRepository
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonalizeViewModel(
    private val productUseCase: ProductUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(PersonalizeViewState())
    val viewState: StateFlow<PersonalizeViewState> = _viewState.asStateFlow()

    init {
        onEvent(PersonalizeViewEvent.LoadCategories)
        onEvent(PersonalizeViewEvent.LoadFavoriteProducts)
    }

    fun onEvent(event: PersonalizeViewEvent) {
        when (event) {
            is PersonalizeViewEvent.LoadCategories -> loadCategories()
            is PersonalizeViewEvent.LoadFavoriteProducts -> loadFavoriteProducts()
            is PersonalizeViewEvent.AddCategory -> addCategory(event.category)
            is PersonalizeViewEvent.RemoveCategory -> removeCategory(event.category)
            is PersonalizeViewEvent.ClearErrorMessage -> clearErrorMessage()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            if (result.isSuccess) {
                _viewState.update { it.copy(categories = result.getOrDefault(emptyList())) }
            } else {
                _viewState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun loadFavoriteProducts() {
        viewModelScope.launch {
            val result = productUseCase.getProducts()
            if (result.isSuccess) {
                _viewState.update {
                    it.copy(
                        favoriteProducts = result.getOrDefault(emptyList())
                            .filter { it.isFavorite })
                }
            }
        }
    }

    private fun addCategory(category: String) {
        viewModelScope.launch {
            val result = categoryRepository.addCategory(category)
            if (result.isSuccess) {
                loadCategories()
            } else {
                _viewState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun removeCategory(category: String) {
        viewModelScope.launch {
            val result = categoryRepository.removeCategory(category)
            if (result.isSuccess) {
                loadCategories()
            } else {
                _viewState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun clearErrorMessage() {
        _viewState.update { it.copy(errorMessage = null) }
    }

    data class PersonalizeViewState(
        val categories: List<String> = emptyList(),
        val favoriteProducts: List<Product> = emptyList(),
        val errorMessage: String? = null
    )

    sealed class PersonalizeViewEvent {
        object LoadCategories : PersonalizeViewEvent()
        object LoadFavoriteProducts : PersonalizeViewEvent()
        data class AddCategory(val category: String) : PersonalizeViewEvent()
        data class RemoveCategory(val category: String) : PersonalizeViewEvent()
        object ClearErrorMessage : PersonalizeViewEvent()
    }
}