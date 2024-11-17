package br.com.angelica.comprainteligente.data.repository.price

import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product

interface PriceRepository {
    suspend fun checkDuplicatePrice(price: Price): Boolean
    suspend fun addPrice(price: Price): Result<Price>
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getPriceHistory(productId: String, period: String, state: String, city: String): Result<List<Price>>
    suspend fun fetchLargestPriceDifference(): Result<String>
    suspend fun fetchTopPrices(): Result<List<String>>
}
