package br.com.angelica.comprainteligente.model

data class ProductWithLatestPrice(
    val product: Product,
    val latestPrice: Price,
    val supermarket: Supermarket
)
