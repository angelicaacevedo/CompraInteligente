package br.com.angelica.comprainteligente.data.repository.lists

import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProductListRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ProductListRepository {

    private val productListCollection = firestore.collection("product_lists")
    private val productCollection = firestore.collection("products")

    override suspend fun fetchProductsByIds(productIds: List<String>): Result<List<Product>> {
        return try {
            if (productIds.isNotEmpty()) {
                val querySnapshot = productCollection
                    .whereIn(FieldPath.documentId(), productIds)
                    .get()
                    .await()

                val products = querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
                Result.success(products)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUserLists(includeProductIds: Boolean): Result<List<ProductList>> {
        return try {
            val querySnapshot = productListCollection
                .orderBy("data", Query.Direction.DESCENDING) // ordena pela data de inserção
                .get()
                .await()

            val lists = querySnapshot.documents.map { document ->
                ProductList(
                    id = document.id,  // Aqui estamos usando o ID gerado pelo Firestore
                    name = document.getString("name") ?: "",
                    productIds = if (includeProductIds) { document.get("productIds") as? List<String> ?: emptyList() } else { emptyList() },
                    data = document.getTimestamp("data") ?: Timestamp.now()
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

    override suspend fun updateList(listId: String, name: String, productIds: List<String>): Result<Unit> {
        return try {
            val document = productListCollection.document(listId)
            document.update(mapOf(
                "name" to name,
                "productIds" to productIds
            )).await()
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