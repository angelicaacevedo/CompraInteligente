package br.com.angelica.comprainteligente.model

import com.google.firebase.Timestamp

data class ProductList(
    val id: String = "",
    val name: String = "",
    val productIds: List<String> = emptyList(),
    var data: Timestamp = Timestamp.now(),
    val userId: String = ""
)