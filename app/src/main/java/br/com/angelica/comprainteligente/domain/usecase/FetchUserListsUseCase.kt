package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepository
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import com.google.firebase.Timestamp

class FetchUserListsUseCase(private val repository: ProductListRepository) {
    suspend fun execute(includeProductIds: Boolean): Result<List<ProductList>> {
        return repository.fetchUserLists(includeProductIds)
    }
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

class UpdateListUseCase(private val productListRepository: ProductListRepository) {
    suspend fun execute(listId: String, name: String, productIds: List<String>, updateData: Timestamp): Result<Unit> {
        return productListRepository.updateList(listId, name, productIds, updateData)
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