package br.com.angelica.comprainteligente.model

import java.security.Timestamp

data class RecentPurchase(
    val marketName: String,
    val totalPrice: Double,
    val date: Timestamp
)