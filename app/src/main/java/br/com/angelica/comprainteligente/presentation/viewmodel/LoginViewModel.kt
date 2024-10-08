package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.LoginUseCase
import br.com.angelica.comprainteligente.utils.ValidatorForm
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> login(intent.email, intent.password)
        }
    }

    private fun login(email: String, password: String) {
        if (!validateInput(email, password)) return

        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = loginUseCase(email, password)
            _state.value = if (result.isSuccess) {
                LoginState.Success(result.getOrNull()!!)
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                LoginState.Error(errorMessage)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (!ValidatorForm.isEmailValid(email)) {
            _emailError.value = "Email inválido"
            isValid = false
        } else {
            _emailError.value = null
        }

        if (!ValidatorForm.isPasswordStrong(password)) {
            _passwordError.value = "A senha deve ter pelo menos 6 caracteres"
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }

    sealed class LoginIntent {
        data class Login(val email: String, val password: String) : LoginIntent()
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: FirebaseUser) : LoginState()
        data class Error(val error: String) : LoginState()
    }
}