package br.com.angelica.comprainteligente.data.repository.price

import br.com.angelica.comprainteligente.model.Price
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PriceRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PriceRepository {

    private val priceCollection = firestore.collection("prices")

    override suspend fun checkDuplicatePrice(price: Price): Boolean {
        val existingPriceQuery = priceCollection
            .whereEqualTo("productId", price.productId)
            .whereEqualTo("supermarketId", price.supermarketId)
            .whereEqualTo("price", price.price)
            .get()
            .await()

        return !existingPriceQuery.isEmpty
    }

    override suspend fun addPrice(price: Price): Result<Price> {
        return try {
            // Adiciona o preço, pois a verificação de duplicidade já foi feita
            val newPriceId = priceCollection.document().id
            price.id = newPriceId
            priceCollection.document(newPriceId).set(price).await()
            Result.success(price) // Retorna o objeto Price recém-criado
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
