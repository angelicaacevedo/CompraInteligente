package br.com.angelica.comprainteligente.data.repository.lists

import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductListRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ProductListRepository {

    private val productListCollection = firestore.collection("product_lists")
    private val productCollection = firestore.collection("products")

    override suspend fun fetchUserLists(): Result<List<ProductList>> {
        return try {
            val querySnapshot = productListCollection.get().await()
            // Mapeie manualmente os documentos, capturando o ID do documento Firestore
            val lists = querySnapshot.documents.map { document ->
                ProductList(
                    id = document.id,  // Aqui estamos usando o ID gerado pelo Firestore
                    name = document.getString("name") ?: "",
                    productIds = document.get("productIds") as? List<String> ?: emptyList()
                )
            }
            Result.success(lists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createList(listName: String, productIds: List<String>): Result<Unit> {
        return try {
            val newListId = productListCollection.document().id
            val newList = ProductList(id = newListId, name = listName, productIds = productIds)
            productListCollection.add(newList).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteList(listId: String): Result<Unit> {
        return try {
            productListCollection.document(listId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSuggestions(query: String): Result<List<Product>> {
        return try {
            val querySnapshot = productCollection
                .whereGreaterThanOrEqualTo("name", query)
                .get()
                .await()
            val products = querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}