package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel : ViewModel() {
    private val _state = MutableStateFlow<NavigationState>(NavigationState.Home)
    val state: StateFlow<NavigationState> = _state

    fun handleIntent(intent: NavigationIntent) {
        when (intent) {
            is NavigationIntent.NavigateToHome -> _state.value = NavigationState.Home
            is NavigationIntent.NavigateToLists -> _state.value = NavigationState.Lists
            is NavigationIntent.NavigateToAddProduct -> _state.value = NavigationState.AddProduct
            is NavigationIntent.NavigateToReports -> _state.value = NavigationState.Reports
            is NavigationIntent.NavigateToProfile -> _state.value = NavigationState.Profile
        }
    }

    sealed class NavigationIntent {
        object NavigateToHome : NavigationIntent()
        object NavigateToLists : NavigationIntent()
        object NavigateToAddProduct : NavigationIntent()
        object NavigateToReports : NavigationIntent()
        object NavigateToProfile : NavigationIntent()
    }

    sealed class NavigationState {
        object Home : NavigationState()
        object Lists : NavigationState()
        object AddProduct : NavigationState()
        object Reports : NavigationState()
        object Profile : NavigationState()
    }
}

