package br.com.angelica.comprainteligente.domain

import br.com.angelica.comprainteligente.data.ProductRepository
import br.com.angelica.comprainteligente.model.Product

class GetProductDetailUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: String): Result<Product> {
        return productRepository.getProductDetails(productId)

    }
}