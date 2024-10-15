package br.com.angelica.comprainteligente.domain

import br.com.angelica.comprainteligente.data.purchase.RecentPurchaseRepository
import br.com.angelica.comprainteligente.model.RecentPurchase

class GetRecentPurchasesUseCase(
    private val recentPurchaseRepository: RecentPurchaseRepository
) {
    suspend operator fun invoke(): Result<List<RecentPurchase>> {
        return recentPurchaseRepository.getRecentPurchases()
    }
}