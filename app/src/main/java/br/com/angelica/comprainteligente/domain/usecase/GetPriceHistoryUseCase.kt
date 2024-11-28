package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository
import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.model.Price

class GetPriceHistoryUseCase(
    private val priceRepository: PriceRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        productId: String,
        period: String,
        userId: String
    ): Result<List<Price>> {
        // Primeiro, obtemos a localização do usuário
        val userResult = authRepository.getUserById(userId)
        if (userResult.isFailure) {
            return Result.failure(
                userResult.exceptionOrNull() ?: Exception("Falha ao obter usuário")
            )
        }

        val user = userResult.getOrNull() ?: return Result.failure(Exception("Usuário não encontrado"))
        val userState = user.address.state
        val userCity = user.address.city

        // Em seguida, usamos essas informações para buscar o histórico de preços
        return priceRepository.getPriceHistory(productId, period, userState, userCity)
    }
}

