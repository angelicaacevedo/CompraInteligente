package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.data.remote.CorreiosApi
import br.com.angelica.comprainteligente.domain.usecase.RegisterUserUseCase
import br.com.angelica.comprainteligente.domain.usecase.LoginUserUseCase
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val correiosApi: CorreiosApi
) : ViewModel() {

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
                    onFailure("Erro ao buscar o endereço")
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
                _authState.value = AuthState.Success  // Alterar o estado para sucesso
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Erro no cadastro")
            }
        }
    }

    // Função para fazer login
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val result = loginUserUseCase.execute(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Falha ao fazer login. Verifique suas credenciais.")
            }
        }
    }

    // Reseta o estado após sucesso ou erro
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    // Estado da autenticação
    sealed class AuthState {
        object Idle : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
