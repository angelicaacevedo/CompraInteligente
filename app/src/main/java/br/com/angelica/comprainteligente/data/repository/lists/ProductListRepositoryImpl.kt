package br.com.angelica.comprainteligente.data.repository.lists

import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.ProductList
import br.com.angelica.comprainteligente.model.ProductWithLatestPrice
import br.com.angelica.comprainteligente.model.Supermarket
import br.com.angelica.comprainteligente.utils.StateMapper
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

    override suspend fun createList(
        listName: String,
        productIds: List<String>,
        userId: String
    ): Result<Unit> {
        return try {
            val newListId = productListCollection.document().id
            val newList = ProductList(
                id = newListId,
                name = listName,
                productIds = productIds,
                userId = userId
            )
            productListCollection.add(newList).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateList(
        listId: String,
        name: String,
        productIds: List<String>,
        data: Timestamp
    ): Result<Unit> {
        return try {
            val document = productListCollection.document(listId)
            document.update(
                mapOf(
                    "name" to name,
                    "productIds" to productIds,
                    "data" to data
                )
            ).await()
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

    override suspend fun fetchProductsByIds(productIds: List<String>): Result<List<Product>> {
        return try {
            if (productIds.isNotEmpty()) {
                val querySnapshot = productCollection
                    .whereIn(FieldPath.documentId(), productIds)
                    .get()
                    .await()

                val products =
                    querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
                Result.success(products)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUserLists(
        includeProductIds: Boolean,
        userId: String
    ): Result<List<ProductList>> {
        return try {
            val querySnapshot = productListCollection
                .whereEqualTo("userId", userId)
                .orderBy("data", Query.Direction.DESCENDING) // ordena pela data de inserção
                .get()
                .await()

            val lists = querySnapshot.documents.map { document ->
                ProductList(
                    id = document.id,  // Aqui estamos usando o ID gerado pelo Firestore
                    name = document.getString("name") ?: "",
                    productIds = if (includeProductIds) {
                        document.get("productIds") as? List<String> ?: emptyList()
                    } else {
                        emptyList()
                    },
                    data = document.getTimestamp("data") ?: Timestamp.now(),
                    userId = userId
                )
            }
            Result.success(lists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchMostRecentAndCheapestPricesByLocation(
        productIds: List<String>,
        userState: String,
        userCity: String
    ): Result<List<ProductWithLatestPrice>> {
        return try {
            // Usa o mapeador para obter o nome completo do estado
            val state = StateMapper.getFullStateName(userState)

            if (productIds.isEmpty()) {
                return Result.failure(Exception("Lista de productsIds vazia"))
            }

            // 1. Consultar a coleção "supermarkets" para obter os IDs dos supermercados na cidade e estado do usuário
            val supermarketsSnapshot = try {
                firestore.collection("supermarkets")
                    .whereEqualTo("state", state)
                    .whereEqualTo("city", userCity)
                    .get()
                    .await()
            } catch (e: Exception) {
                return Result.failure(Exception("Erro ao buscar supermercados para a localização especificada."))
            }

            val supermarketIds = supermarketsSnapshot.documents.mapNotNull { it.id }
            if (supermarketIds.isEmpty()) {
                return Result.failure(Exception("Nenhum supermercado encontrado para a localização especificada."))
            }

            // 2. Consultar a coleção "prices" usando os productIds e supermarketIds
            val pricesSnapshot = try {
                firestore.collection("prices")
                    .whereIn("productId", productIds)
                    .whereIn("supermarketId", supermarketIds)
                    .orderBy("date", Query.Direction.DESCENDING)  // Ordena por data mais recente
                    .orderBy(
                        "price",
                        Query.Direction.ASCENDING
                    )   // Ordena por preço mais baixo em caso de empate
                    .get()
                    .await()
            } catch (e: Exception) {
                return Result.failure(Exception("Erro ao buscar preços para os produtos e localização especificados."))
            }

            // Mapeia preços para cada productId e busca o mais recente e mais barato
            val groupedPrices = pricesSnapshot.documents.groupBy { it.getString("productId") }
            val productWithPrices = mutableListOf<ProductWithLatestPrice>()

            for ((productId, priceDocs) in groupedPrices) {
                // Como já ordenamos por data (mais recente) e preço (mais baixo), podemos pegar o primeiro da lista
                val bestPriceDoc = priceDocs.firstOrNull()
                val price = bestPriceDoc?.toObject(Price::class.java)

                if (price != null) {
                    // Obter detalhes do produto
                    val productSnapshot = try {
                        firestore.collection("products").document(productId!!).get().await()
                    } catch (e: Exception) {
                        return Result.failure(Exception("Erro ao buscar detalhes do produto."))
                    }

                    val product = Product(
                        id = productId,
                        name = productSnapshot.getString("name") ?: "Produto desconhecido",
                        imageUrl = productSnapshot.getString("imageUrl") ?: ""
                    )

                    // Obter detalhes do supermercado
                    val supermarket =
                        supermarketsSnapshot.documents.find { it.id == price.supermarketId }
                            ?.toObject(Supermarket::class.java)

                    if (supermarket != null) {
                        productWithPrices.add(
                            ProductWithLatestPrice(
                                product = product,
                                latestPrice = price,
                                supermarket = supermarket
                            )
                        )
                    }
                }
            }

            Result.success(productWithPrices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
