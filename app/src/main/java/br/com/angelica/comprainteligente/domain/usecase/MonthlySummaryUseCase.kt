package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.model.MonthlySummaryState

class MonthlySummaryUseCase(
    private val priceRepository: PriceRepository
) {
    suspend operator fun invoke(userId: String): Result<MonthlySummaryState> {
        return try {
            val currentMonthPrices = priceRepository.getPricesForCurrentMonth(userId)
            val totalSpent = currentMonthPrices.sumOf { it.price }

            Result.success(MonthlySummaryState(totalSpent = totalSpent))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
