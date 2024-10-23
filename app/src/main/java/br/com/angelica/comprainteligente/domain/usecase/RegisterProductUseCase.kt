package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.utils.toProduct

class RegisterProductUseCase(private val productRepository: ProductRepository) {

    suspend fun execute(product: Product, price: Price): Result<Unit> {
        return try {
            // Verifica se o produto já existe no Firestore
            val productResult = productRepository.getProductInfoFromBarcode(product.id)

            // Se o produto já existe, registra apenas o preço, senão cadastra o produto e o preço
            if (productResult.isSuccess) {
                val existingProductDetails = productResult.getOrNull()

                if (existingProductDetails != null) {
                    // Produto já existe, converte ProductDetails em Product
                    val existingProduct = existingProductDetails.toProduct(product.id)
                    productRepository.registerProduct(existingProduct, price)
                } else {
                    // Produto não existe, então cadastra o produto e o preço
                    productRepository.registerProduct(product, price)
                }
            } else {
                // Caso de erro na consulta ao produto
                Result.failure(Exception("Erro ao consultar o produto."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

