package br.com.angelica.comprainteligente.data

import br.com.angelica.comprainteligente.model.Product

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun addProduct(product: Product): Result<Unit>
}