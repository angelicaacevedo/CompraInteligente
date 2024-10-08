package br.com.angelica.comprainteligente.data.auth

import br.com.angelica.comprainteligente.model.UserProfile
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun getUserProfile(userId: String): UserProfile?
    suspend fun updateUserProfile(userProfile: UserProfile): Result<UserProfile>
}