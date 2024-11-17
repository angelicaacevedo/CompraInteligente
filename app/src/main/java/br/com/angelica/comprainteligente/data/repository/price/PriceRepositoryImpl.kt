package br.com.angelica.comprainteligente.data.repository.price

import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.utils.StateMapper
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

    override suspend fun getPriceHistory(productId: String, period: String, state: String, city: String): Result<List<Price>> {
        return try {
            // Usa o mapeador para obter o nome completo do estado
            val userState = StateMapper.getFullStateName(state)

            // Define a data de início para o filtro de período
            val startDate = calculateStartDate(period)

            // 1. Consultar a coleção "supermarkets" para obter os IDs dos supermercados na cidade e estado do usuário
            val supermarketsSnapshot = try {
                firestore.collection("supermarkets")
                    .whereEqualTo("state", userState)
                    .whereEqualTo("city", city)
                    .get()
                    .await()
            } catch (e: Exception) {
                return Result.failure(Exception("Erro ao buscar supermercados para a localização especificada."))
            }

            // Obter IDs dos supermercados encontrados
            val supermarketIds = supermarketsSnapshot.documents.mapNotNull { it.id }
            if (supermarketIds.isEmpty()) {
                return Result.failure(Exception("Nenhum supermercado encontrado para a localização especificada."))
            }

            // 2. Consultar a coleção "prices" usando o productId e os IDs dos supermercados
            val pricesSnapshot = try {
                firestore.collection("prices")
                    .whereEqualTo("productId", productId)
                    .whereIn("supermarketId", supermarketIds)
                    .whereGreaterThan("date", startDate)
                    .orderBy("date", Query.Direction.ASCENDING)
                    .get()
                    .await()
            } catch (e: Exception) {
                return Result.failure(Exception("Erro ao buscar histórico de preços para o produto especificado e localização."))
            }

            // Converter resultados para objetos `Price`
            val prices = pricesSnapshot.toObjects(Price::class.java)

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
            // Filtrar por região do usuario logado

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
