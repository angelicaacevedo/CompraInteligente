package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> login(intent.email, intent.password)
            is LoginIntent.EmailChanged -> _email.value = intent.email
            is LoginIntent.PasswordChanged -> _password.value = intent.password
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            // Simular uma chamada de autenticação
            if (email == "teste@example.com" && password == "password") {
                _state.value = LoginState.Success
            } else {
                _state.value = LoginState.Error("Invalid credentials")
            }
        }
    }
}

sealed class LoginIntent {
    data class Login(val email: String, val password: String) : LoginIntent()
    data class EmailChanged(val email: String) : LoginIntent()
    data class PasswordChanged(val password: String) : LoginIntent()
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}