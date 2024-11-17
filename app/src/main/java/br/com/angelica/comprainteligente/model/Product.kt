package br.com.angelica.comprainteligente.model

import com.google.firebase.Timestamp

data class Product(
    val id: String = "",                   // ID único do produto
    val name: String = "",                 // Nome do produto
    val imageUrl: String = "",              // URL da imagem do produto
    val updateData: Timestamp = Timestamp.now(), // Data de atualização do produto
    var userId: String = ""
)
