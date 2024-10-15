package br.com.angelica.comprainteligente.data.product

import br.com.angelica.comprainteligente.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreProductRepository : ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getProducts(): Result<List<Product>> {
        return executeFirestoreOperation {
            val snapshot = db.collection("products").get().await()
            snapshot.documents.map { it.toObject(Product::class.java)!! }
        }
    }

    override suspend fun addProduct(product: Product): Result<Unit> {
        return executeFirestoreOperation {
            val newProductRef = db.collection("products").document()
            val productWithId = product.copy(id = newProductRef.id)
            newProductRef.set(productWithId).await()
            Unit
        }
    }

    override suspend fun getPriceHistory(productName: String): Result<List<Product>> {
        return executeFirestoreOperation {
            val snapshot = db.collection("products")
                .whereEqualTo("name", productName)
                .orderBy("timestamp")
                .get()
                .await()
            snapshot.documents.map { it.toObject(Product::class.java)!! }
        }
    }

    override suspend fun removeProduct(productId: String): Result<Unit> {
        return executeFirestoreOperation {
            db.collection("products").document(productId).delete().await()
            Unit
        }
    }

    override suspend fun changeProductFavoriteStatus(
        productId: String,
        isFavorite: Boolean
    ): Result<Unit> {
        return executeFirestoreOperation {
            db.collection("products").document(productId).update("isFavorite", isFavorite).await()
            Unit
        }
    }

    private suspend fun <T> executeFirestoreOperation(operation: suspend () -> T): Result<T> {
        return try {
            Result.success(operation())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}