package br.com.angelica.comprainteligente.domain.usecase

import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.model.UserProgressState

class UserProgressUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(userId: String): Result<UserProgressState> {
        return try {
            val productCount = productRepository.countUserProducts(userId)
            val points = productCount * 10 // Cada produto vale 10 pontos, por exemplo
            val level = points / 200 + 1 // Cada nível tem 200 pontos
            val progress = (points % 200) / 200f

            Result.success(UserProgressState(points = points, level = level, progress = progress))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
