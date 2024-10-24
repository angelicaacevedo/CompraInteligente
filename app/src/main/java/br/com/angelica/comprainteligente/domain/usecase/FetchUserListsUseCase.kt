package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepository
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList

class FetchUserListsUseCase(private val repository: ProductListRepository) {
    suspend fun execute(): Result<List<ProductList>> = repository.fetchUserLists()
}

class CreateListUseCase(private val repository: ProductListRepository) {
    suspend fun execute(listName: String, products: List<Product>): Result<Unit> {
        return repository.createList(listName, products)
    }
}

class GetProductSuggestionsUseCase(private val repository: ProductListRepository) {
    suspend fun execute(query: String): Result<List<Product>> {
        return repository.getSuggestions(query)
    }
}