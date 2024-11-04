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

    // Função para carregar dados do usuário
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authUseCases.getUserById(userId)
            _userData.value = result.getOrNull()
            _isLoading.value = false
        }
    }

    // Função para atualizar os dados do usuário
    fun updateUserProfile(userId: String, updatedUser: User) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authUseCases.updateUserInfo(userId, updatedUser, updatedUser.address)
            if (result.isSuccess) {
                _userData.value = updatedUser // Atualiza o estado com o novo perfil
            }
            _isLoading.value = false
        }
    }

    // Função para logout
    fun logout() {
        viewModelScope.launch {
            authUseCases.logout()
        }
    }
}

