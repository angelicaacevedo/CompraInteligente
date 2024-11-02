package br.com.angelica.comprainteligente.data.repository.auth

import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User

interface AuthRepository {
    suspend fun register(user: User): Result<String>
    suspend fun login(email: String, password: String): Result<String>
    suspend fun updateUserInfo(userId: String, updatedUser: User, address: Address): Result<Unit>
    suspend fun getUserById(userId: String): Result<User>
    suspend fun logout()
}
