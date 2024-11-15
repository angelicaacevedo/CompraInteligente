package br.com.angelica.comprainteligente.model

data class User(
    val id: String,                   // ID único do usuário
    val username: String,             // Nome de usuário
    val email: String,                // Email do usuário
    val passwordHash: String,         // Senha criptografada
    val address: Address              // Objeto Address com as informações de endereço do usuário
)
