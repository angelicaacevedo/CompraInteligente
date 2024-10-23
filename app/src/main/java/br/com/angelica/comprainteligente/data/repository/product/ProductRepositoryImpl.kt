package br.com.angelica.comprainteligente.data.repository.product

import br.com.angelica.comprainteligente.data.remote.OpenFoodFactsApi
import br.com.angelica.comprainteligente.model.Category
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.remote.ProductDetails
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val openFoodFactsApi: OpenFoodFactsApi // Adicionando a API
) : ProductRepository {

    private val productCollection = firestore.collection("products")
    private val supermarketCollection = firestore.collection("supermarkets")
    private val priceCollection = firestore.collection("prices")

    override suspend fun getProductInfoFromBarcode(barcode: String): Result<ProductDetails> {
        return try {
            // Verifica se o produto já existe no Firestore
            val querySnapshot = productCollection.whereEqualTo("id", barcode).get().await()
            if (!querySnapshot.isEmpty) {
                val product = querySnapshot.documents.first().toObject(Product::class.java)
                Result.success(ProductDetails(product?.name, null, product?.imageUrl))
            } else {
                // Caso o produto não esteja no Firestore, busca os detalhes da API
                val response = openFoodFactsApi.getProductByBarcode(barcode)
                if (response.status == 1 && response.product != null) {
                    Result.success(response.product)
                } else {
                    Result.failure(Exception("Produto não encontrado na API."))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerProduct(product: Product, price: Price): Result<Unit> {
        return try {
            // Verifica se o produto já está cadastrado no Firestore
            val productResult = productCollection.document(product.id).get().await()

            if (!productResult.exists()) {
                // Cadastra o produto na coleção de produtos
                productCollection.document(product.id).set(product).await()
            }

            // Verifica se o supermercado já está cadastrado
            val supermarketId = price.supermarketId
            val supermarketQuery = supermarketCollection.whereEqualTo("id", supermarketId).get().await()

            if (supermarketQuery.isEmpty) {
                // Se o supermercado não existir, cadastra-o
                val newSupermarketId = supermarketCollection.document().id
                val newSupermarket = mapOf(
                    "id" to newSupermarketId,
                    "name" to price.supermarketId  // Ajuste para incluir os dados corretos
                )
                supermarketCollection.document(newSupermarketId).set(newSupermarket).await()

                // Atualiza o ID do supermercado no preço
                price.supermarketId = newSupermarketId
            }

            // Gerar um novo ID para o preço
            val newPriceId = priceCollection.document().id
            price.id = newPriceId

            // Cadastra o preço na coleção de preços
            priceCollection.document(newPriceId).set(price).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val categoriesSnapshot = firestore.collection("categories").get().await()
            val categories = categoriesSnapshot.documents.mapNotNull { document ->
                document.toObject(Category::class.java)
            }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}