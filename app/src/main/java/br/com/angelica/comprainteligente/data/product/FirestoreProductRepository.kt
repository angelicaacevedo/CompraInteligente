package br.com.angelica.comprainteligente.data.product

import br.com.angelica.comprainteligente.model.Product
import com.google.firebase.Timestamp
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
            val productWithTimestamp = product.copy(timestamp = Timestamp.now())
            db.collection("products").add(productWithTimestamp).await()
            Unit
        }
    }

    override suspend fun getProductDetails(productId: String): Result<Product> {
        return executeFirestoreOperation {
            val document = db.collection("products").document(productId).get().await()
            document.toObject(Product::class.java) ?: throw Exception("Produto não encontrado")
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

    private suspend fun <T> executeFirestoreOperation(operation: suspend () -> T): Result<T> {
        return try {
            Result.success(operation())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}