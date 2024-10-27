package br.com.angelica.comprainteligente.data.repository.price

import br.com.angelica.comprainteligente.model.Price

interface PriceRepository {
    suspend fun checkDuplicatePrice(price: Price): Boolean
    suspend fun addPrice(price: Price): Result<Price>
}
