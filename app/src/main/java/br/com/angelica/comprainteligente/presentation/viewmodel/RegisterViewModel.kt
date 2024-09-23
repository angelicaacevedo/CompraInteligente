package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.RegisterUseCase
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

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
            val result = registerUseCase(email, password)
            _state.value = if (result.isSuccess) {
                RegisterState.Success(result.getOrNull()!!)
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    sealed class RegisterIntent {
        data class Register(val email: String, val password: String, val confirmPassword: String) :
            RegisterIntent()

        data class Error(val message: String) : RegisterIntent()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val user: FirebaseUser) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}