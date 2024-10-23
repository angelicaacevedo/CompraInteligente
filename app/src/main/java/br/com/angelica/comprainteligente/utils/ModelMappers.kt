package br.com.angelica.comprainteligente.utils

import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.remote.ProductDetails

fun ProductDetails.toProduct(barcode: String): Product {
    return Product(
        id = barcode,
        name = this.product_name ?: "",
        imageUrl = this.image_url ?: "",
        // Outros campos podem ser ajustados conforme necessário
    )
}