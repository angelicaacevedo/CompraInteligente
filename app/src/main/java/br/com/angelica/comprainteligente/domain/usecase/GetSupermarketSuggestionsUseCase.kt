package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepository

class GetSupermarketSuggestionsUseCase(private val repository: SupermarketRepository) {
    suspend fun execute(query: String): List<String> {
        return repository.getSupermarketSuggestions(query)
    }
}