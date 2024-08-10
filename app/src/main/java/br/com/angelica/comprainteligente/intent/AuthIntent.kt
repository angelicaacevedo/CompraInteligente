package br.com.angelica.comprainteligente.intent

sealed class AuthIntent {
    data class EnterEmail(val email: String) : AuthIntent()
    data class EnterPassword(val password: String) : AuthIntent()
    object Login : AuthIntent()
    object Register : AuthIntent()
}