package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepository
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList

class FetchUserListsUseCase(private val repository: ProductListRepository) {
    suspend fun execute(): Result<List<ProductList>> = repository.fetchUserLists()
}

class FetchProductsByListUseCase(private val repository: ProductListRepository) {
    suspend fun execute(productIds: List<String>): Result<List<Product>> {
        return repository.fetchProductsByIds(productIds)
    }
}

class CreateListUseCase(private val repository: ProductListRepository) {
    suspend fun execute(listName: String, productIds: List<String>): Result<Unit> {
        return repository.createList(listName, productIds)
    }
}

class DeleteListUseCase(private val repository: ProductListRepository) {
    suspend fun execute(listId: String): Result<Unit> {
        return repository.deleteList(listId)
    }
}

class GetProductSuggestionsUseCase(private val repository: ProductListRepository) {
    suspend fun execute(query: String): Result<List<Product>> {
        return repository.getSuggestions(query)
    }
}