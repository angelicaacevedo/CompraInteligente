package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository
import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User

class RegisterUserUseCase(private val authRepository: AuthRepository) {

    suspend fun execute(user: User, address: Address): Result<Unit> {
        return authRepository.register(user, address)
    }
}
