package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.RegisterUseCase
import br.com.angelica.comprainteligente.presentation.common.Validator
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    // Estados de erro
    var emailError: String? by mutableStateOf(null)
    var passwordError: String? by mutableStateOf(null)
    var confirmPasswordError: String? by mutableStateOf(null)

    fun processIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.Register -> register(
                intent.email,
                intent.password,
                intent.confirmPassword
            )
            is RegisterIntent.Error -> _state.value = RegisterState.Error(intent.message)
        }
    }

    private fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading

            // Resetar mensagens de erro
            emailError = null
            passwordError = null
            confirmPasswordError = null

            // Validações
            if (!Validator.isEmailValid(email)) {
                emailError = "Email inválido"
            }

            if (!Validator.isPasswordStrong(password)) {
                passwordError = "A senha deve ter pelo menos 6 caracteres"
            }

            if (password != confirmPassword) {
                confirmPasswordError = "As senhas não correspondem"
            }

            // Se houver erros, atualizar o estado e retornar
            if (emailError != null || passwordError != null || confirmPasswordError != null) {
                _state.value = RegisterState.Error("Por favor, corrija os erros acima.")
                return@launch
            }

            // Registro do usuário
            val result = registerUseCase(email, password)
            _state.value = if (result.isSuccess) {
                RegisterState.Success(result.getOrNull()!!)
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    sealed class RegisterIntent {
        data class Register(val email: String, val password: String, val confirmPassword: String) : RegisterIntent()
        data class Error(val message: String) : RegisterIntent()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val user: FirebaseUser) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}