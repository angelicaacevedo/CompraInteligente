package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository

class GetLargestPriceDifferenceUseCase(
    private val repository: PriceRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.fetchLargestPriceDifference()
    }
}