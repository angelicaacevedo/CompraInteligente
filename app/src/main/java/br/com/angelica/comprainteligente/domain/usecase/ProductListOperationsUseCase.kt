package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepository
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.model.ProductWithLatestPrice
import com.google.firebase.Timestamp

class ProductListOperationsUseCase(private val repository: ProductListRepository) {

    // Método para buscar listas de um usuário, com opção de incluir IDs de produtos
    suspend fun fetchUserLists(
        includeProductIds: Boolean,
        userId: String
    ): Result<List<ProductList>> {
        return repository.fetchUserLists(includeProductIds, userId)
    }

    // Método para buscar produtos por lista de IDs
    suspend fun fetchProductsByList(productIds: List<String>): Result<List<Product>> {
        return repository.fetchProductsByIds(productIds)
    }

    // Método para criar uma nova lista
    suspend fun createList(
        listName: String,
        productIds: List<String>,
        userId: String
    ): Result<Unit> {
        return repository.createList(listName, productIds, userId)
    }

    // Método para atualizar uma lista existente
    suspend fun updateList(
        listId: String,
        name: String,
        productIds: List<String>,
        updateData: Timestamp
    ): Result<Unit> {
        return repository.updateList(listId, name, productIds, updateData)
    }

    // Método para deletar uma lista
    suspend fun deleteList(listId: String): Result<Unit> {
        return repository.deleteList(listId)
    }

    // Método para obter sugestões de produtos
    suspend fun getProductSuggestions(query: String): Result<List<Product>> {
        return repository.getSuggestions(query)
    }

    // Método para buscar os últimos preços de produtos em uma lista
    suspend fun fetchLatestPricesForList(productIds: List<String>): Result<List<ProductWithLatestPrice>> {
        return repository.fetchLatestPricesForList(productIds)
    }
}
