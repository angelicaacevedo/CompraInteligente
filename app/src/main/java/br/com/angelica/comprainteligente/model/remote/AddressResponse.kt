package br.com.angelica.comprainteligente.model.remote

data class AddressResponse(
    val logradouro: String,
    val bairro: String,
    val localidade: String,
    val uf: String,
    val cep: String
)
