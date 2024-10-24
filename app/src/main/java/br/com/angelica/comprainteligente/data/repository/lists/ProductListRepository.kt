package br.com.angelica.comprainteligente.data.repository.lists

import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList

interface ProductListRepository {
    suspend fun fetchUserLists(): Result<List<ProductList>>
    suspend fun createList(listName: String, productIds: List<String>): Result<Unit>
    suspend fun deleteList(listId: String): Result<Unit>
    suspend fun getSuggestions(query: String): Result<List<Product>>
}