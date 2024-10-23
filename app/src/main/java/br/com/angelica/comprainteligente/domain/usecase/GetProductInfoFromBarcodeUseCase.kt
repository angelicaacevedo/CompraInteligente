package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.model.remote.ProductDetails

class GetProductInfoFromBarcodeUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(barcode: String): Result<ProductDetails> {
        return productRepository.getProductInfoFromBarcode(barcode)
    }
}
