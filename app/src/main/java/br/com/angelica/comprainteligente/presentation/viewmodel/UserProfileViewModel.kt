package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.AuthUseCases
import br.com.angelica.comprainteligente.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Função para carregar dados do usuário usando o UseCase
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authUseCases.getUserById(userId) // Usando o UseCase para obter os dados do usuário
            _userData.value = result.getOrNull()
            _isLoading.value = false
        }
    }

    // Função para logout usando o UseCase
    fun logout() {
        viewModelScope.launch {
            authUseCases.logout()
        }
    }
}

