package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepository
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.remote.ProductDetails
import java.util.UUID

class ProductOperationsUseCase(
    private val productRepository: ProductRepository,
    private val supermarketRepository: SupermarketRepository,
    private val priceRepository: PriceRepository
) {

    // Função para obter informações do produto a partir do código de barras
    suspend fun getProductInfoFromBarcode(barcode: String): Result<ProductDetails> {
        return productRepository.getProductInfoFromBarcode(barcode)
    }

    // Função para obter todos os produtos
    suspend fun getProducts(): Result<List<Product>> {
        return priceRepository.getProducts()
    }


    // Função para registrar um produto
    suspend fun registerProduct(product: Product, price: Price, placeId: String): Result<Product> {
        return try {
            // Verifica ou cria o supermercado utilizando o placeId
            val supermarketResult =
                supermarketRepository.checkOrCreateSupermarket(placeId, price.supermarketId)
            if (supermarketResult.isFailure) {
                return Result.failure(supermarketResult.exceptionOrNull()!!)
            }

            val supermarket = supermarketResult.getOrNull()
            price.supermarketId = supermarket?.id
                ?: return Result.failure(Exception("Erro ao obter ID do supermercado."))

            // Verifica se o produto é manual (sem código de barras) ou automático (com código de barras)
            val productId = product.id.ifBlank {
                UUID.randomUUID().toString() // Gera um ID para produtos manuais
            }

            val updatedProduct = product.copy(id = productId)

            // Registra o produto
            val productResult = productRepository.registerProduct(updatedProduct)
            if (productResult.isFailure) {
                return productResult
            }

            // Atualiza o productId no objeto Price
            price.productId = productId

            // Verifica duplicação de preço e registra o preço, se necessário
            if (!priceRepository.checkDuplicatePrice(price)) {
                val addedPriceResult = priceRepository.addPrice(price)
                if (addedPriceResult.isFailure) {
                    return Result.failure(addedPriceResult.exceptionOrNull()!!)
                }
            } else {
                return Result.failure(Exception("Esse produto com o mesmo supermercado e preço já está cadastrado."))
            }

            productResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerPrice(price: Price): Result<Price> {
        return priceRepository.addPrice(price)
    }
}
