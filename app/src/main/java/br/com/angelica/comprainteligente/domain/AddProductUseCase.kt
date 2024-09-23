package br.com.angelica.comprainteligente.domain

import br.com.angelica.comprainteligente.data.ProductRepository
import br.com.angelica.comprainteligente.model.Product

class AddProductUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(product: Product): Result<Unit> {
        return productRepository.addProduct(product)
    }
}
