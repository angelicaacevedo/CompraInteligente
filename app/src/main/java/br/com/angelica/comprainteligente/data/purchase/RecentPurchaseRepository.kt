package br.com.angelica.comprainteligente.data.purchase

import br.com.angelica.comprainteligente.model.RecentPurchase

interface RecentPurchaseRepository {
    suspend fun getRecentPurchases(): Result<List<RecentPurchase>>
}