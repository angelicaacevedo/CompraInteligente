package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.GetPriceHistoryUseCase
import br.com.angelica.comprainteligente.model.Price
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InflationViewModel(
    private val getPriceHistoryUseCase: GetPriceHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InflationViewState())
    val state: StateFlow<InflationViewState> = _state.asStateFlow()

    fun handleIntent(intent: InflationIntent) {
        when (intent) {
            is InflationIntent.LoadPriceHistory -> loadPriceHistory(intent.productId)
        }
    }

    private fun loadPriceHistory(productId: String) {
        viewModelScope.launch {
            _state.value = InflationViewState(isLoading = true)
            val result = getPriceHistoryUseCase(productId)

            result.onSuccess { prices ->
                _state.value = InflationViewState(prices = prices)
            }.onFailure { error ->
                _state.value = InflationViewState(error = error.message)
            }
        }
    }

    sealed class InflationIntent {
        data class LoadPriceHistory(val productId: String) : InflationIntent()
    }

    data class InflationViewState(
        val isLoading: Boolean = false,
        val prices: List<Price> = emptyList(),
        val error: String? = null
    )
}
