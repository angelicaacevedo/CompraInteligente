package br.com.angelica.comprainteligente.data

import br.com.angelica.comprainteligente.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreProductRepository : ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val snapshot = db.collection("products").get().await()
            val products = snapshot.documents.map { it.toObject(Product::class.java)!! }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProduct(product: Product): Result<Unit> {
        return try {
            db.collection("products").add(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}