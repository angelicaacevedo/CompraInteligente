package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User

class AuthUseCases(private val authRepository: AuthRepository) {

    // Função para registrar usuário
    suspend fun registerUser(user: User): Result<String> {
        return authRepository.register(user)
    }

    // Função para fazer login
    suspend fun loginUser(email: String, password: String): Result<String> {
        return authRepository.login(email, password)
    }

    // Função para atualizar informações do usuário
    suspend fun updateUserInfo(userId: String, user: User, address: Address): Result<Unit> {
        return authRepository.updateUserInfo(userId, user, address)
    }

    // Função para buscar dados do usuário pelo ID
    suspend fun getUserById(userId: String): Result<User> {
        return authRepository.getUserById(userId)
    }

    // Função para logout
    suspend fun logout() {
        authRepository.logout()
    }
}
