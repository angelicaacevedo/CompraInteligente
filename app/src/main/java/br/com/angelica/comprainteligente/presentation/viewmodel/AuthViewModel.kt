package br.com.angelica.comprainteligente.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.data.remote.CorreiosApi
import br.com.angelica.comprainteligente.domain.usecase.LoginUserUseCase
import br.com.angelica.comprainteligente.domain.usecase.RegisterUserUseCase
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val correiosApi: CorreiosApi,
) : AndroidViewModel(application) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

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
                    val errorBody =
                        response.errorBody()?.string()  // Armazena o corpo de erro como string
                    onFailure("Erro ao buscar o endereço: ${errorBody ?: "Erro desconhecido"}")  //
                }
            } catch (e: Exception) {
                onFailure(e.message ?: "Erro desconhecido")
            }
        }
    }

    // Função para registrar o usuário
    fun registerUser(user: User, address: Address) {
        viewModelScope.launch {
            val result = registerUserUseCase.execute(user, address)
            if (result.isSuccess) {
                val userId = result.getOrNull() ?: ""
                saveUserIdToPreferences(userId)
                _authState.value = AuthState.Success(userId)  // Alterar o estado para sucesso
            } else {
                _authState.value =
                    AuthState.Error(result.exceptionOrNull()?.message ?: "Erro no cadastro")
            }
        }
    }

    // Função para fazer login
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val result = loginUserUseCase.execute(email, password)
            if (result.isSuccess) {
                val userId = result.getOrNull() ?: ""
                saveUserIdToPreferences(userId)
                _authState.value = AuthState.Success(userId)
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.message
                        ?: "Falha ao fazer login. Verifique suas credenciais."
                )
            }
        }
    }

    // Reseta o estado após sucesso ou erro
    fun resetAuthState() {
        _authState.value = AuthState.Idle
        println("Auth state reset to Idle")  // Log para verificar o reset
    }

    // Função para salvar o userId no SharedPreferences
    private fun saveUserIdToPreferences(userId: String) {
        val sharedPref =
            getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("userId", userId)
            apply()
        }
    }

    // Estado da autenticação
    sealed class AuthState {
        object Idle : AuthState()
        data class Success(val userId: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
