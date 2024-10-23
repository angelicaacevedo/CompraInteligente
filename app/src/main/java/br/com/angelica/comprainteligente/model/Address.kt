package br.com.angelica.comprainteligente.model

data class Address(
    val id: String = "",                   // ID único do endereço
    val street: String = "",               // Nome da rua
    val number: String = "",               // Número da residência
    val neighborhood: String = "",         // Bairro
    val city: String = "",                 // Cidade
    val state: String = "",                // Estado
    val postalCode: String = ""           // CEP
)
