package br.com.angelica.comprainteligente.presentation.viewModel.login

sealed class LoginIntent {
    data class Login(val email: String, val password: String) : LoginIntent()
    data class Register(val email: String, val password: String) : LoginIntent()
}