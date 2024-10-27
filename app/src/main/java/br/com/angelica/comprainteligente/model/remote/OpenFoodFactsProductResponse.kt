package br.com.angelica.comprainteligente.model.remote

data class OpenFoodFactsProductResponse(
    val product: ProductDetails?,
    val status: Int,
    val status_verbose: String
)

data class ProductDetails(
    val product_name: String?,
    val brands: String?,
    val image_url: String?
)