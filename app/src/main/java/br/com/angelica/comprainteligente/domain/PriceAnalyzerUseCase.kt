package br.com.angelica.comprainteligente.domain

import br.com.angelica.comprainteligente.data.price.PriceAnalyzer
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.SupermarketComparisonResult

class PriceAnalyzerUseCase(
    private val priceAnalyzer: PriceAnalyzer
) {
    suspend fun analyzePrices(shoppingList: List<Product>): Result<List<SupermarketComparisonResult>> {
        return priceAnalyzer.analyze(shoppingList)
    }
}
