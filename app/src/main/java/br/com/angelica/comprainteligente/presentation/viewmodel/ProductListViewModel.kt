package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.CreateListUseCase
import br.com.angelica.comprainteligente.domain.usecase.FetchUserListsUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetProductSuggestionsUseCase
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val fetchUserListsUseCase: FetchUserListsUseCase,
    private val createListUseCase: CreateListUseCase,
    private val getProductSuggestionsUseCase: GetProductSuggestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProductListState>(ProductListState.Idle)
    val state: StateFlow<ProductListState> = _state

    fun handleIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.LoadLists -> loadUserLists()
            is ProductListIntent.CreateNewList -> createNewList(intent.name, intent.products)
            is ProductListIntent.GetProductSuggestions -> fetchProductSuggestions(intent.query)
        }
    }

    private fun loadUserLists() {
        viewModelScope.launch {
            _state.value = ProductListState.Loading
            val result = fetchUserListsUseCase.execute()
            if (result.isSuccess) {
                _state.value = ProductListState.ListsLoaded(result.getOrNull() ?: emptyList())
            } else {
                _state.value = ProductListState.Error("Failed to load lists")
            }
        }
    }

    private fun createNewList(name: String, products: List<Product>) {
        viewModelScope.launch {
            _state.value = ProductListState.Loading
            val result = createListUseCase.execute(name, products)
            if (result.isSuccess) {
                _state.value = ProductListState.ListCreated(true)
            } else  {
                _state.value = ProductListState.Error("Failed to create list")
            }
        }
    }

    private fun fetchProductSuggestions(query: String) {
        viewModelScope.launch {
            _state.value = ProductListState.Loading
            val result = getProductSuggestionsUseCase.execute(query)
            if (result.isSuccess) {
                _state.value = ProductListState.SuggestionsLoaded(result.getOrNull() ?: emptyList())
            } else {
                _state.value = ProductListState.Error("Failed to load suggestions")
            }
        }
    }

    sealed class ProductListIntent {
        object LoadLists : ProductListIntent()
        data class CreateNewList(val name: String, val products: List<Product>) : ProductListIntent()
        data class GetProductSuggestions(val query: String) : ProductListIntent()
    }

    sealed class ProductListState {
        object Idle : ProductListState()
        object Loading : ProductListState()
        data class ListsLoaded(val lists: List<ProductList>) : ProductListState()
        data class ListCreated(val success: Boolean) : ProductListState()
        data class SuggestionsLoaded(val suggestions: List<Product>) : ProductListState()
        data class Error(val message: String) : ProductListState()
    }
}