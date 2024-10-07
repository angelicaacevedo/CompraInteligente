package br.com.angelica.comprainteligente.data.price

import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.SupermarketComparisonResult

interface PriceAnalyzer {
    suspend fun analyze(shoppingList: List<Product>): Result<List<SupermarketComparisonResult>>
}