package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepository
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.remote.ProductDetails

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
    suspend fun registerProduct(product: Product, price: Price): Result<Product> {
        return try {
            // Verifica ou cria o supermercado e obtém o ID
            val supermarketResult = supermarketRepository.checkOrCreateSupermarket(price.supermarketId, price.supermarketId)
            if (supermarketResult.isFailure) return Result.failure(supermarketResult.exceptionOrNull()!!)

            val supermarket = supermarketResult.getOrNull()
            price.supermarketId = supermarket?.id ?: return Result.failure(Exception("Erro ao obter ID do supermercado."))

            // Registra o produto se ele ainda não existir
            val productResult = productRepository.registerProduct(product)
            if (productResult.isFailure) return productResult

            // Verifica duplicação de preço e registra o preço, se necessário
            if (!priceRepository.checkDuplicatePrice(price)) {
                val addedPriceResult = priceRepository.addPrice(price)
                if (addedPriceResult.isFailure) return Result.failure(addedPriceResult.exceptionOrNull()!!)
            } else {
                return Result.failure(Exception("Esse produto com o mesmo supermercado e preço já está cadastrado."))
            }

            productResult // Retorna o resultado do produto
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}