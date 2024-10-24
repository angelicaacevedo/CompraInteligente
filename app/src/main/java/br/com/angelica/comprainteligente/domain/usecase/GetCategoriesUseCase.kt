package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.model.Category

class GetCategoriesUseCase(private val productRepository: ProductRepository) {

//    suspend fun execute(): Result<List<Category>> {
//        return productRepository.getCategories()
//    }
}