package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.model.Product

class GetProductsUseCase(
    private val priceRepository: PriceRepository
) {
    suspend operator fun invoke(): Result<List<Product>> {
        return priceRepository.getProducts()
    }
}