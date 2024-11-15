package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.model.Category
import br.com.angelica.comprainteligente.model.CategoryRepository

class GetCategoriesUseCase(private val categoryRepository: CategoryRepository) {
    fun execute(): List<Category> {
        return categoryRepository.categories
    }
}