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

    suspend fun getPriceHistory(productName: String): Result<List<Product>> {
        return productRepository.getPriceHistory(productName)
    }

    suspend fun removeProduct(productId: String): Result<Unit> {
        return productRepository.removeProduct(productId)
    }

    suspend fun changeProductFavoriteStatus(productId: String, isFavorite: Boolean): Result<Unit> {
        return productRepository.changeProductFavoriteStatus(productId, isFavorite)
    }
}

