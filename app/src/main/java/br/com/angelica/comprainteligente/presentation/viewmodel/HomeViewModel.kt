package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.GetLargestPriceDifferenceUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetTopPricesUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetUserLevelUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getLargestPriceDifferenceUseCase: GetLargestPriceDifferenceUseCase,
    private val getTopPricesUseCase: GetTopPricesUseCase,
    private val getUserLevelUseCase: GetUserLevelUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadHomeData -> loadHomeData()
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val priceDifferenceResult = getLargestPriceDifferenceUseCase()
                val topPricesResult = getTopPricesUseCase()
                val userLevelResult = getUserLevelUseCase()

                _state.value = HomeState(
                    isLoading = false,
                    priceDifferenceProduct = priceDifferenceResult.getOrNull(),
                    topPrices = topPricesResult.getOrNull() ?: emptyList(),
                    userLevel = userLevelResult.getOrNull()?.level ?: "Nível 1",
                    userProgress = userLevelResult.getOrNull()?.progress ?: 0
                )
            } catch (e: Exception) {
                _state.value = HomeState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    // HomeIntent.kt
    sealed class HomeIntent {
        object LoadHomeData : HomeIntent()
    }

    // HomeState.kt
    data class HomeState(
        val isLoading: Boolean = false,
        val priceDifferenceProduct: String? = null,
        val topPrices: List<String> = emptyList(),
        val userLevel: String = "Nível 1",
        val userProgress: Int = 0,
        val error: String? = null
    )
}
