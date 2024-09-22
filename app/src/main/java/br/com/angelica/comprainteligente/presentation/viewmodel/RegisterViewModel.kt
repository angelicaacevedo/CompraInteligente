package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    fun processIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.Register -> register(intent.email, intent.password, intent.confirmPassword)
            is RegisterIntent.EmailChanged -> _email.value = intent.email
            is RegisterIntent.PasswordChanged -> _password.value = intent.password
            is RegisterIntent.ConfirmPasswordChanged -> _confirmPassword.value = intent.confirmPassword
        }
    }

    private fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            // Simular uma chamada de registro
            if (password != confirmPassword) {
                _state.value = RegisterState.Error("Passwords do not match")
            } else if (email.isNotEmpty() && password.isNotEmpty()) {
                // Simular sucesso no registro
                _state.value = RegisterState.Success
            } else {
                _state.value = RegisterState.Error("Invalid input")
            }
        }
    }

    sealed class RegisterIntent {
        data class Register(val email: String, val password: String, val confirmPassword: String) : RegisterIntent()
        data class EmailChanged(val email: String) : RegisterIntent()
        data class PasswordChanged(val password: String) : RegisterIntent()
        data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterIntent()
    }

    sealed class RegisterState {
        object Idle: RegisterState()
        object Loading: RegisterState()
        object Success: RegisterState()
        data class Error(val message: String): RegisterState()
    }
}