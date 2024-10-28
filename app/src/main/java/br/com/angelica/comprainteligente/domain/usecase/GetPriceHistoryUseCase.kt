package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.model.Price

class GetPriceHistoryUseCase(
    private val priceRepository: PriceRepository
) {
    suspend operator fun invoke(productId: String): Result<List<Price>> {
        return priceRepository.getPriceHistory(productId)
    }
}
