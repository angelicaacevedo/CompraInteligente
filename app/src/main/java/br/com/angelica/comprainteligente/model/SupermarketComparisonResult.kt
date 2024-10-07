package br.com.angelica.comprainteligente.model

data class SupermarketComparisonResult(
    val supermarketName: String,
    val totalPrice: Double,
    val distance: Double,
    val isBestChoice: Boolean
)