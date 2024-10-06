package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.PriceAnalyzerUseCase
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.SupermarketComparisonResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListsViewModel(
    private val productUseCase: ProductUseCase,
    private val priceAnalyzerUseCase: PriceAnalyzerUseCase
) : ViewModel() {
    // Estados para os produtos
    private val _state = MutableStateFlow<ListState>(ListState.Loading)
    val state: StateFlow<ListState> = _state

    // Estados para a análise de preços
    private val _priceAnalysisState = MutableStateFlow<PriceAnalysisState>(PriceAnalysisState.Idle)
    val priceAnalysisState: StateFlow<PriceAnalysisState> = _priceAnalysisState

    // Carregar produtos
    fun loadProducts() {
        viewModelScope.launch {
            val result = productUseCase.getProducts()
            _state.value = if (result.isSuccess) {
                ListState.Success(result.getOrNull() ?: emptyList())
            } else {
                ListState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    // Adicionar produto à lista de compras
    fun addProductToShoppingList(product: Product) {
        viewModelScope.launch {
            productUseCase.addProduct(product)
            loadProducts() // Recarrega a lista após adicionar
        }
    }

    // Analisar preços
    fun analyzePrices(shoppingList: List<Product>) {
        viewModelScope.launch {
            val result = priceAnalyzerUseCase.analyzePrices(shoppingList)
            _priceAnalysisState.value = if (result.isSuccess) {
                PriceAnalysisState.Success(result.getOrNull() ?: emptyList())
            } else {
                PriceAnalysisState.Error(result.exceptionOrNull()?.message ?: "Erro ao analisar preços")
            }
        }
    }

    fun removeProduct(product: Product) {
        viewModelScope.launch {
            productUseCase.removeProduct(product) // Remove o produto do caso de uso
            loadProducts() // Recarrega a lista após remover
        }
    }

    // Estados da lista
    sealed class ListState {
        object Loading : ListState()
        data class Success(val products: List<Product>) : ListState()
        data class Error(val message: String) : ListState()
    }

    // Estados da análise de preços
    sealed class PriceAnalysisState {
        object Idle : PriceAnalysisState()
        data class Success(val result: List<SupermarketComparisonResult>) : PriceAnalysisState()
        data class Error(val message: String) : PriceAnalysisState()
    }
}