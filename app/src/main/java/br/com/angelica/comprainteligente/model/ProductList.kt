package br.com.angelica.comprainteligente.model

import com.google.firebase.Timestamp

data class ProductList(
    val id: String = "",
    val name: String = "",
    val productIds: List<String> = emptyList(),
    val data: Timestamp = Timestamp.now()
)