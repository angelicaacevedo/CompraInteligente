package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository

class GetTopPricesUseCase(
    private val repository: PriceRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.fetchTopPrices()
    }
}