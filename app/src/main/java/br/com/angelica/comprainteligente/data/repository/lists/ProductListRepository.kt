package br.com.angelica.comprainteligente.data.repository.lists

import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.model.ProductWithLatestPrice
import com.google.firebase.Timestamp

interface ProductListRepository {
    suspend fun createList(listName: String, productIds: List<String>, userId: String): Result<Unit>
    suspend fun updateList(listId: String, name: String, productIds: List<String>, data: Timestamp): Result<Unit>
    suspend fun deleteList(listId: String): Result<Unit>
    suspend fun getSuggestions(query: String): Result<List<Product>>
    suspend fun fetchProductsByIds(productIds: List<String>): Result<List<Product>>
    suspend fun fetchUserLists(includeProductIds: Boolean, userId: String): Result<List<ProductList>>
    suspend fun fetchMostRecentAndCheapestPricesByLocation(productIds: List<String>, userState: String, userCity: String): Result<List<ProductWithLatestPrice>>
}