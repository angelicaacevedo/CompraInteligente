package br.com.angelica.comprainteligente.data.repository.price

import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PriceRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PriceRepository {

    private val productCollection = firestore.collection("products")
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

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val products = productCollection
                .get()
                .await()
                .toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPriceHistory(productId: String, period: String): Result<List<Price>> {
        return try {
            // Define o intervalo de datas com base no período selecionado
            val startDate = when (period) {
                "7 dias" -> System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                "1 mês" -> System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
                "6 meses" -> System.currentTimeMillis() - (6 * 30L * 24 * 60 * 60 * 1000)
                "1 ano" -> System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000)
                "5 anos" -> System.currentTimeMillis() - (5 * 365L * 24 * 60 * 60 * 1000)
                else -> System.currentTimeMillis()
            }

            val prices = priceCollection
                .whereEqualTo("productId", productId) // Filtro pelo produto específico
                .whereGreaterThan("date", startDate) // Filtro pelo período selecionado
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Price::class.java)

            Result.success(prices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
