package br.com.angelica.comprainteligente.data.repository.product

import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.model.remote.ProductDetails

interface ProductRepository {
    suspend fun getProductInfoFromBarcode(barcode: String): Result<ProductDetails>
    suspend fun registerProduct(product: Product): Result<Product>
}

