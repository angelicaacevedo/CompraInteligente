package br.com.angelica.comprainteligente.data.purchase

import br.com.angelica.comprainteligente.model.RecentPurchase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRecentPurchaseRepository : RecentPurchaseRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getRecentPurchases(): Result<List<RecentPurchase>> {
        return try {
            val snapshot = db.collection("purchases").get().await()
            val purchases = snapshot.documents.map { it.toObject(RecentPurchase::class.java)!! }
            Result.success(purchases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
