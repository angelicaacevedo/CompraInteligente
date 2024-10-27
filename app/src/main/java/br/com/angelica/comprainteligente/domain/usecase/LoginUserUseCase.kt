package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository

class LoginUserUseCase(private val authRepository: AuthRepository) {
    suspend fun execute(email: String, password: String): Result<String> {
        return authRepository.login(email, password)
    }
}