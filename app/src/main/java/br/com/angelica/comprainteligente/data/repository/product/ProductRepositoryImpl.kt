package br.com.angelica.comprainteligente.data.repository.product

import br.com.angelica.comprainteligente.data.remote.OpenFoodFactsApi
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.remote.ProductDetails
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val openFoodFactsApi: OpenFoodFactsApi // Adicionando a API
) : ProductRepository {

    private val productCollection = firestore.collection("products")
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

    override suspend fun registerProduct(product: Product): Result<Product> {
        return try {
            val productResult = productCollection.document(product.id).get().await()
            if (!productResult.exists()) {
                // Cadastra o produto na coleção de produtos se ele ainda não existir
                productCollection.document(product.id).set(product).await()
            }
            Result.success(product) // Retorna o produto cadastrado ou encontrado
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
