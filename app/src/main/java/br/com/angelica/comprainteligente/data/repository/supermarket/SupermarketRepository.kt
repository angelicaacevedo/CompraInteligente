package br.com.angelica.comprainteligente.data.repository.supermarket

import br.com.angelica.comprainteligente.model.Supermarket

interface SupermarketRepository {
    suspend fun getSupermarketSuggestions(query: String): List<Pair<String, String>>
    suspend fun checkOrCreateSupermarket(placeId: String, name: String): Result<Supermarket>
    suspend fun getAllSupermarkets(): List<Supermarket>
}
