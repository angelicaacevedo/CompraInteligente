package br.com.angelica.comprainteligente.data.repository.supermarket

interface SupermarketRepository {
    suspend fun getSupermarketSuggestions(query: String): List<String>
}