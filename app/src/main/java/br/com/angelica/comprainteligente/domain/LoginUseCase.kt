package br.com.angelica.comprainteligente.domain

import br.com.angelica.comprainteligente.data.AuthRepository
import com.google.firebase.auth.FirebaseUser

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        return authRepository.login(email, password)
    }
}