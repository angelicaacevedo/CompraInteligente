package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.model.Price

class RecentPurchasesUseCase(
    private val priceRepository: PriceRepository
) {
    suspend operator fun invoke(userId: String, limit: Int): Result<List<Price>> {
        return try {
            // Chama o repositório para obter as compras recentes
            val recentPurchases = priceRepository.getRecentPurchases(userId, limit)
            Result.success(recentPurchases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
