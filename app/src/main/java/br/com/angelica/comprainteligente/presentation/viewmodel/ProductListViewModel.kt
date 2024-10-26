package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.CreateListUseCase
import br.com.angelica.comprainteligente.domain.usecase.DeleteListUseCase
import br.com.angelica.comprainteligente.domain.usecase.FetchLatestPricesForListUseCase
import br.com.angelica.comprainteligente.domain.usecase.FetchProductsByListUseCase
import br.com.angelica.comprainteligente.domain.usecase.FetchUserListsUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetProductSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.UpdateListUseCase
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.model.ProductWithLatestPrice
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val fetchUserListsUseCase: FetchUserListsUseCase,
    private val createListUseCase: CreateListUseCase,
    private val deleteListUseCase: DeleteListUseCase,
    private val getProductSuggestionsUseCase: GetProductSuggestionsUseCase,
    private val fetchProductsByListUseCase: FetchProductsByListUseCase,
    private val updateListUseCase: UpdateListUseCase,
    private val fetchLatestPricesForListUseCase: FetchLatestPricesForListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProductListState>(ProductListState.Idle)
    val state: StateFlow<ProductListState> = _state

    init {
        loadUserLists()
    }

    fun handleIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.LoadLists -> loadUserLists(includeProductIds = true)
            is ProductListIntent.LoadListsWithoutProductIds -> loadUserLists(includeProductIds = false)
            is ProductListIntent.CreateOrUpdateList -> createOrUpdateList(
                intent.listId,
                intent.name,
                intent.productIds
            )

            is ProductListIntent.GetProductSuggestions -> fetchProductSuggestions(intent.query)
            is ProductListIntent.DeleteList -> deleteList(intent.listId)
            is ProductListIntent.ViewProductsInList -> {
                if (intent.loadLatestPrices) {
                    loadLatestPricesForList(intent.productIds)
                } else {
                    loadProductsFromList(intent.productIds)
                }
            }
        }
    }

    private fun loadUserLists(includeProductIds: Boolean = true) {
        viewModelScope.launch {
            _state.value = ProductListState.Loading
            val result = fetchUserListsUseCase.execute(includeProductIds)
            if (result.isSuccess) {
                _state.value = ProductListState.ListsLoaded(result.getOrNull() ?: emptyList())
            } else {
                _state.value = ProductListState.Error("Failed to load lists")
            }
        }
    }

    private fun createOrUpdateList(listId: String?, name: String, productIds: List<String>) {
        viewModelScope.launch {
            val updateData = Timestamp.now()

            val result = if (listId == null) {
                // Criação de uma nova lista
                createListUseCase.execute(name, productIds)
            } else {
                // Atualização de uma lista existente
                updateListUseCase.execute(listId, name, productIds, updateData)
            }

            if (result.isSuccess) {
                _state.value = ProductListState.ListCreated(true)
            } else {
                _state.value = ProductListState.Error("Failed to create list")
            }
        }
    }

    private fun loadProductsFromList(productIds: List<String>) {
        if (productIds.isEmpty()) {
            _state.value = ProductListState.Empty
            return
        }
        viewModelScope.launch {
            _state.value = ProductListState.Loading
            val result = fetchProductsByListUseCase.execute(productIds)

            if (result.isSuccess) {
                val products = result.getOrNull().orEmpty()
                if (products.isNotEmpty()) {
                    _state.value = ProductListState.ProductsLoaded(products)
                } else {
                    _state.value = ProductListState.Empty
                }
            } else {
                _state.value = ProductListState.Error("Failed to load products")
            }
        }
    }

    private fun deleteList(listId: String) {
        viewModelScope.launch {
            val result = deleteListUseCase.execute(listId)
            if (result.isSuccess) {
                loadUserLists()
            } else {
                _state.value = ProductListState.Error("Failed to delete list")
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

    private fun loadLatestPricesForList(productIds: List<String>) {
        viewModelScope.launch {
            _state.value = ProductListState.Loading
            val result = fetchLatestPricesForListUseCase.execute(productIds)
            if (result.isSuccess) {
                _state.value =
                    ProductListState.ProductsWithLatestPricesLoaded(result.getOrNull().orEmpty())
            } else {
                _state.value = ProductListState.Error("Failed to load latest prices")
            }
        }
    }

    fun resetState() {
        _state.value = ProductListState.Idle
    }

    sealed class ProductListIntent {
        object LoadLists : ProductListIntent()
        object LoadListsWithoutProductIds : ProductListIntent()
        data class CreateOrUpdateList(
            val listId: String?,
            val name: String,
            val productIds: List<String>
        ) : ProductListIntent()

        data class DeleteList(val listId: String) : ProductListIntent()
        data class GetProductSuggestions(val query: String) : ProductListIntent()

        // Adicionando uma flag para especificar se queremos carregar preços
        data class ViewProductsInList(
            val productIds: List<String>,
            val loadLatestPrices: Boolean = false
        ) : ProductListIntent()
    }

    sealed class ProductListState {
        object Idle : ProductListState()
        object Loading : ProductListState()
        object Empty : ProductListState()
        data class ListsLoaded(val lists: List<ProductList>) : ProductListState()
        data class ListCreated(val success: Boolean) : ProductListState()
        data class SuggestionsLoaded(val suggestions: List<Product>) : ProductListState()
        data class ProductsLoaded(val products: List<Product>) : ProductListState()
        data class ProductsWithLatestPricesLoaded(val products: List<ProductWithLatestPrice>) :
            ProductListState()

        data class Error(val message: String) : ProductListState()
    }
}