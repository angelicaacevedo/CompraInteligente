package br.com.angelica.comprainteligente.data.category

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreCategoryRepository : CategoryRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getCategories(): Result<List<String>> {
        return executeFirestoreOperation {
            val snapshot = db.collection("categories").get().await()
            snapshot.documents.map { it.getString("name")!! }
        }
    }

    override suspend fun addCategory(category: String): Result<Unit> {
        return executeFirestoreOperation {
            val categoryLowerCase = category.lowercase()
            val existingCategories = getCategories().getOrDefault(emptyList())
            if (existingCategories.any { it.lowercase() == categoryLowerCase }) {
                throw Exception("Categoria já existe")
            }
            db.collection("categories").add(mapOf("name" to category)).await()
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

    override suspend fun removeCategory(category: String): Result<Unit> {
        return executeFirestoreOperation {
            val snapshot = db.collection("categories")
                .whereEqualTo("name", category)
                .get()
                .await()
            for (document in snapshot.documents) {
                db.collection("categories").document(document.id).delete().await()
            }
        }
    }
}