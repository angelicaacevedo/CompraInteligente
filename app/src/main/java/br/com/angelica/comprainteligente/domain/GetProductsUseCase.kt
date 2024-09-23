package br.com.angelica.comprainteligente.domain

import br.com.angelica.comprainteligente.data.ProductRepository
import br.com.angelica.comprainteligente.model.Product

class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(): Result<List<Product>> {
        return productRepository.getProducts()
    }
}