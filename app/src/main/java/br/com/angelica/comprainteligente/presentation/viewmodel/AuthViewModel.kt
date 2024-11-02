package br.com.angelica.comprainteligente.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.data.remote.CorreiosApi
import br.com.angelica.comprainteligente.domain.usecase.AuthUseCases
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val authUseCases: AuthUseCases,
    private val correiosApi: CorreiosApi,
) : AndroidViewModel(application) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Função para buscar o endereço com base no CEP
    fun fetchAddressByCep(cep: String, onSuccess: (Address) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = correiosApi.getAddressByCep(cep)
                if (response.isSuccessful) {
                    val addressResponse = response.body()
                    if (addressResponse != null) {
                        val address = Address(
                            street = addressResponse.logradouro,
                            number = "",
                            neighborhood = addressResponse.bairro,
                            city = addressResponse.localidade,
                            state = addressResponse.uf,
                            postalCode = addressResponse.cep
                        )
                        onSuccess(address)
                    } else {
                        onFailure("Endereço não encontrado")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    onFailure("Erro ao buscar o endereço: ${errorBody ?: "Erro desconhecido"}")
                }
            } catch (e: Exception) {
                onFailure(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun registerUser(username: String, email: String, password: String, address: Address) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = User(
                id = "",
                username = username,
                email = email,
                passwordHash = password,
                address = address
            )
            val result = authUseCases.registerUser(user)
            if (result.isSuccess) {
                val userId = result.getOrNull() ?: ""
                saveUserIdToPreferences(userId)
                _authState.value = AuthState.Success(userId)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Erro no cadastro")
            }
            _isLoading.value = false
        }
    }

    // Função para fazer login
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authUseCases.loginUser(email, password)
            if (result.isSuccess) {
                val userId = result.getOrNull() ?: ""
                saveUserIdToPreferences(userId)
                _authState.value = AuthState.Success(userId)
            } else {
                _authState.value =
                    AuthState.Error("Seu e-mail e senha estão incorretos. Verifique suas informações e tente novamente.")
            }
            _isLoading.value = false
        }
    }

    // Salva o userId no SharedPreferences
    private fun saveUserIdToPreferences(userId: String) {
        val sharedPref = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_id", userId)
            apply()
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    // Estado da autenticação
    sealed class AuthState {
        object Idle : AuthState()
        data class Success(val userId: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
