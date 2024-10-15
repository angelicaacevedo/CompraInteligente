package br.com.angelica.comprainteligente.model

import com.google.firebase.Timestamp

data class Product(
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isFavorite: Boolean = false
)
