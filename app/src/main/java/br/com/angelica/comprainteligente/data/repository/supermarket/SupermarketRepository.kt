package br.com.angelica.comprainteligente.data.repository.supermarket

import br.com.angelica.comprainteligente.model.Supermarket

interface SupermarketRepository {
    suspend fun getSupermarketSuggestions(query: String): List<String>
    suspend fun checkOrCreateSupermarket(supermarketId: String, name: String): Result<Supermarket>
}
