package br.com.angelica.comprainteligente.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.intent.AuthIntent
import br.com.angelica.comprainteligente.model.AuthState
import br.com.angelica.comprainteligente.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> get() = _state

    private val _intent = Channel<AuthIntent>(Channel.UNLIMITED)
    val intent = _intent.receiveAsFlow()

    init {
        handleIntent()
    }

    fun sendIntent(intent: AuthIntent) {
        viewModelScope.launch {
            _intent.send(intent)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.collect { authIntent ->
                when (authIntent) {
                    is AuthIntent.EnterEmail -> {
                        _state.value = _state.value.copy(email = authIntent.email)
                    }

                    is AuthIntent.EnterPassword -> {
                        _state.value = _state.value.copy(password = authIntent.password)
                    }

                    is AuthIntent.Login -> {
                        login()
                    }

                    is AuthIntent.Register -> {
                        register()
                    }
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.login(_state.value.email, _state.value.password)
            _state.value = _state.value.copy(
                isLoading = false,
                isAuthenticated = result,
                error = if (result) null else "Login failed"
            )
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.register(_state.value.email, _state.value.password)
            _state.value = _state.value.copy(
                isLoading = false,
                isAuthenticated = result,
                error = if (result) null else "Registration failed"
            )
        }
    }
}