package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.MonthlySummaryUseCase
import br.com.angelica.comprainteligente.domain.usecase.ProductOperationsUseCase
import br.com.angelica.comprainteligente.domain.usecase.RecentPurchasesUseCase
import br.com.angelica.comprainteligente.domain.usecase.UserProgressUseCase
import br.com.angelica.comprainteligente.model.MonthlySummaryState
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.UserProgressState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val monthlySummaryUseCase: MonthlySummaryUseCase,
    private val recentPurchasesUseCase: RecentPurchasesUseCase,
    private val userProgressUseCase: UserProgressUseCase,
    private val productOperationsUseCase: ProductOperationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadHomeData -> loadHomeData(intent.userId)
            HomeIntent.ClearError -> clearError()
        }
    }

    private fun loadHomeData(userId: String) {
        loadMonthlySummary(userId)
        loadRecentPurchases(userId)
        loadUserProgress(userId)
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val result = productOperationsUseCase.getProducts()
            result.onSuccess { productList ->
                val productMap = productList.associateBy { it.id }
                _state.value = _state.value.copy(
                    products = productMap,
                    error = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(error = error.message)
            }
        }
    }

    private fun loadMonthlySummary(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = monthlySummaryUseCase(userId)
            result.onSuccess { summary ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    monthlySummary = summary,
                    error = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    private fun loadRecentPurchases(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = recentPurchasesUseCase(userId, limit = 5)
            result.onSuccess { purchases ->
                val purchasesWithNames = purchases.map { price ->
                    price to (_state.value.products[price.productId]?.name
                        ?: "Produto desconhecido")
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    recentPurchases = purchasesWithNames,
                    error = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    private fun loadUserProgress(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = userProgressUseCase(userId)
            result.onSuccess { progress ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    userProgress = progress,
                    error = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    sealed class HomeIntent {
        data class LoadHomeData(val userId: String) : HomeIntent()
        object ClearError : HomeIntent()
    }

    data class HomeViewState(
        val isLoading: Boolean = false,
        val monthlySummary: MonthlySummaryState? = null,
        val recentPurchases: List<Pair<Price, String>> = emptyList(),
        val userProgress: UserProgressState? = null,
        val products: Map<String, Product> = emptyMap(),
        val error: String? = null
    )
}

