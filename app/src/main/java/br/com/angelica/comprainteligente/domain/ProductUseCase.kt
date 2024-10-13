package br.com.angelica.comprainteligente.domain

import br.com.angelica.comprainteligente.data.product.ProductRepository
import br.com.angelica.comprainteligente.model.Product

class ProductUseCase(
    private val productRepository: ProductRepository
) {
    suspend fun getProducts(): Result<List<Product>> {
        return productRepository.getProducts()
    }

    suspend fun addProduct(product: Product): Result<Unit> {
        return productRepository.addProduct(product)
    }

    suspend fun getProductDetails(productId: String): Result<Product> {
        return productRepository.getProductDetails(productId)
    }
    suspend fun getPriceHistory(productName: String): Result<List<Product>> {
        return productRepository.getPriceHistory(productName)
    }
}

