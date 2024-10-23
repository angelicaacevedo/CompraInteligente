package br.com.angelica.comprainteligente.data.repository.auth

import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User

interface AuthRepository {
    suspend fun register(user: User, address: Address): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
}
