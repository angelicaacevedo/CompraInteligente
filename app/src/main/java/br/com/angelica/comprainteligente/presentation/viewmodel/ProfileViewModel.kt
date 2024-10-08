package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.data.auth.AuthRepository
import br.com.angelica.comprainteligente.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val userProfile = authRepository.getUserProfile(user?.uid ?: "")
            _state.value = if (user != null && userProfile != null) {
                ProfileState.Success(userProfile)
            } else {
                ProfileState.Error("Usuário não encontrado")
            }
        }
    }

    fun updateUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            val result = authRepository.updateUserProfile(userProfile)
            _state.value = if (result.isSuccess) {
                ProfileState.Success(result.getOrNull()!!)
            } else {
                ProfileState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    sealed class ProfileState {
        object Loading : ProfileState()
        data class Success(val userProfile: UserProfile) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }
}