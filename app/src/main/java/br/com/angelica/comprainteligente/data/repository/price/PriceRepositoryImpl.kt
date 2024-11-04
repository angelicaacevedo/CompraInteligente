package br.com.angelica.comprainteligente.data.repository.price

import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar

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
            val newPriceId = priceCollection.document().id
            price.id = newPriceId
            priceCollection.document(newPriceId).set(price).await()
            Result.success(price)
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
            val startDate = calculateStartDate(period)

            val prices = priceCollection
                .whereEqualTo("productId", productId)
                .whereGreaterThan("date", startDate)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(Price::class.java)

            Result.success(prices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateStartDate(period: String): Timestamp {
        val calendar = Calendar.getInstance()
        when (period) {
            "7 dias" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            "1 mês" -> calendar.add(Calendar.MONTH, -1)
            "6 meses" -> calendar.add(Calendar.MONTH, -6)
            "1 ano" -> calendar.add(Calendar.YEAR, -1)
            "5 anos" -> calendar.add(Calendar.YEAR, -5)
        }
        val startDate = Timestamp(calendar.time)
        return startDate
    }

    override suspend fun fetchLargestPriceDifference(): Result<String> {
        return try {
            // Buscar preços ordenados
            val pricesSnapshot = priceCollection.orderBy("price").get().await()
            val prices = pricesSnapshot.toObjects(Price::class.java)

            // Encontrar a maior diferença de preço entre produtos
            val maxDifferenceProduct = prices
                .groupBy { it.productId }
                .maxByOrNull { it.value.maxOf { p -> p.price } - it.value.minOf { p -> p.price } }
                ?.let { group ->
                    val minPrice = group.value.minByOrNull { it.price }?.price
                    val maxPrice = group.value.maxByOrNull { it.price }?.price

                    // Buscar o nome do produto pelo ID
                    val productName = productCollection.document(group.key).get().await().getString("name")

                    "$productName: R$$minPrice - R$$maxPrice"
                }

            Result.success(maxDifferenceProduct ?: "Nenhuma diferença encontrada")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchTopPrices(): Result<List<String>> {
        return try {
            val topPricesSnapshot = priceCollection
                .orderBy("price")
                .limit(10)
                .get()
                .await()

            // Mapear os IDs dos produtos para seus nomes
            val topPrices = topPricesSnapshot.documents.mapNotNull { document ->
                val productId = document.getString("productId") ?: ""
                val price = document.getDouble("price") ?: 0.0

                // Buscar o nome do produto pelo ID
                val productName = productCollection.document(productId).get().await().getString("name")
                "$productName - R$$price"
            }

            Result.success(topPrices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
