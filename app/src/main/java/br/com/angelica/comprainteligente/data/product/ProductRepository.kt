package br.com.angelica.comprainteligente.data.product

import br.com.angelica.comprainteligente.model.Product

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun addProduct(product: Product): Result<Unit>
    suspend fun getProductDetails(productId: String): Result<Product>
    suspend fun getPriceHistory(productName: String): Result<List<Product>> // Adicionado método para obter histórico de preços
}