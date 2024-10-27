package br.com.angelica.comprainteligente.data.repository.product

import br.com.angelica.comprainteligente.data.remote.OpenFoodFactsApi
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
            // Define o userId nos objetos product e price
            val userId = price.userId
            product.userId = userId
            price.userId = userId

            // Verifica se o produto já está cadastrado no Firestore
            val productResult = productCollection.document(product.id).get().await()

            if (!productResult.exists()) {
                // Cadastra o produto na coleção de produtos
                productCollection.document(product.id).set(product).await()
            }

            // Verifica se o supermercado já está cadastrado
            val supermarketQuery = supermarketCollection.whereEqualTo("id", price.supermarketId).get().await()
            val supermarketId = if (supermarketQuery.isEmpty) {
                // Se o supermercado não existir, cadastra-o e retorna o novo ID
                val newSupermarketId = supermarketCollection.document().id
                val newSupermarket = mapOf(
                    "id" to newSupermarketId,
                    "name" to price.supermarketId
                )
                supermarketCollection.document(newSupermarketId).set(newSupermarket).await()
                newSupermarketId
            } else {
                // Se o supermercado já existir, retorna o ID existente
                supermarketQuery.documents.first().id
            }

            // Verifica se já existe um preço para o mesmo produto, supermercado e valor
            val existingPriceQuery = priceCollection
                .whereEqualTo("productId", price.productId)
                .whereEqualTo("supermarketId", supermarketId)
                .whereEqualTo("value", price.price)
                .get()
                .await()

            if (!existingPriceQuery.isEmpty) {
                // Se o preço já existe, retorna um erro
                return Result.failure(Exception("Esse produto com o mesmo supermercado e preço já está cadastrado."))
            }

            // Gerar um novo ID para o preço
            val newPriceId = priceCollection.document().id
            price.id = newPriceId
            price.supermarketId = supermarketId

            // Cadastra o preço na coleção de preços
            priceCollection.document(newPriceId).set(price).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}