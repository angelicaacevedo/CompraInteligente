package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository
import br.com.angelica.comprainteligente.model.UserLevel

class GetUserLevelUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<UserLevel> {
        return repository.getUserLevel()
    }
}