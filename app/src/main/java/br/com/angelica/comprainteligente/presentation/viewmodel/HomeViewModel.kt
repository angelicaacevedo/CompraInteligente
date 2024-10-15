package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.GetRecentPurchasesUseCase
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.RecentPurchase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productUseCase: ProductUseCase,
    private val getRecentPurchasesUseCase: GetRecentPurchasesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            val productsResult = productUseCase.getProducts()
            val highlightedProducts =
                productsResult.getOrNull()?.sortedBy { it.price }?.take(5) ?: emptyList()
            val recentPurchasesResult = getRecentPurchasesUseCase()
            val recentPurchases = recentPurchasesResult.getOrDefault(emptyList())

            _state.value = HomeState(
                totalSpent = calculateTotalSpent(recentPurchases),
                highlightedProducts = highlightedProducts,
                recentPurchases = recentPurchases
            )
        }
    }

    private fun calculateTotalSpent(recentPurchases: List<RecentPurchase>): Double {
        return recentPurchases.sumOf { it.totalPrice }
    }
}

data class HomeState(
    val totalSpent: Double = 0.0,
    val highlightedProducts: List<Product> = emptyList(),
    val recentPurchases: List<RecentPurchase> = emptyList()
)