package br.com.angelica.comprainteligente.model

import com.google.firebase.Timestamp

data class Price(
    var id: String = "",
    val productId: String = "",
    var supermarketId: String = "",
    val price: Double = 0.0,
    val date: Timestamp = Timestamp.now(),
    val validationCount: Long = 0,
    var userId: String = ""
)
