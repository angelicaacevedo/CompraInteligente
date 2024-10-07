package br.com.angelica.comprainteligente.data.price

import br.com.angelica.comprainteligente.data.product.ProductRepository
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.SupermarketComparisonResult

class PriceAnalyzerRepository(
    private val productRepository: ProductRepository
) : PriceAnalyzer {

    override suspend fun analyze(shoppingList: List<Product>): Result<List<SupermarketComparisonResult>> {
        return try {
            // 1. Obter lista completa de produtos em diferentes supermercados
            val productsInSupermarkets = productRepository.getProducts().getOrNull()
                ?: return Result.failure(Exception("Erro ao carregar produtos"))

            // 2. Filtrar produtos que estão na lista de compras
            val filteredProducts = productsInSupermarkets.filter { product ->
                shoppingList.any { it.name == product.name }
            }

            // 3. Calcular o preço total em cada supermercado
            val supermarketResults = calculateSupermarketPrices(filteredProducts, shoppingList)

            // 4. Determinar o melhor supermercado
            val bestSupermarket = selectBestSupermarket(supermarketResults)

            Result.success(bestSupermarket)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateSupermarketPrices(
        products: List<Product>,
        shoppingList: List<Product>
    ): List<SupermarketComparisonResult> {
        val supermarketTotals = mutableMapOf<String, Double>()

        // Para cada supermercado, calcular o preço total da lista de compras
        products.groupBy { it.name }.forEach { (supermarket, productsAtSupermarket) ->
            val totalPrice = shoppingList.sumOf { shoppingProduct ->
                val productPrice = productsAtSupermarket.find { it.name == shoppingProduct.name }?.price
                productPrice ?: 0.0
            }
            supermarketTotals[supermarket] = totalPrice
        }

        // Aqui seria adicionada lógica para calcular distâncias, mas estamos simulando
        return supermarketTotals.map { (supermarket, totalPrice) ->
            SupermarketComparisonResult(
                supermarketName = supermarket,
                totalPrice = totalPrice,
                distance = calculateDistanceToSupermarket(supermarket), // Simulação de distância
                isBestChoice = false // Este valor será ajustado posteriormente
            )
        }
    }

    private fun calculateDistanceToSupermarket(supermarket: String): Double {
        // Aqui entraria a lógica para calcular a distância entre o supermercado e o usuário
        // Exemplo simplificado:
        return Math.random() * 10 // Simulando distâncias aleatórias
    }

    private fun selectBestSupermarket(results: List<SupermarketComparisonResult>): List<SupermarketComparisonResult> {
        val sortedByPrice = results.sortedBy { it.totalPrice }
        val bestResult = sortedByPrice.firstOrNull()

        return results.map {
            it.copy(isBestChoice = it == bestResult)
        }
    }
}