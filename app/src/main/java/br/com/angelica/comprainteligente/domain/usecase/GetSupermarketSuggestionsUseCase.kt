package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepository
import br.com.angelica.comprainteligente.model.Supermarket

class GetSupermarketSuggestionsUseCase(private val repository: SupermarketRepository) {
    suspend fun execute(query: String): List<Pair<String, String>> {
        return repository.getSupermarketSuggestions(query)
    }

    suspend operator fun invoke(): Result<List<Supermarket>> {
        return try {
            val supermarkets = repository.getAllSupermarkets()
            Result.success(supermarkets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}