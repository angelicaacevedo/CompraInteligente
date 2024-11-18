package br.com.angelica.comprainteligente.data.repository.auth

import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import br.com.angelica.comprainteligente.model.UserLevel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun register(user: User): Result<String> {
        return try {
            // Registra o usuário no Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(user.email, user.passwordHash).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")

            // Define os dados do usuário, incluindo o campo 'address' completo
            val userData = hashMapOf(
                "id" to userId,
                "username" to user.username,
                "email" to user.email,
                "address" to hashMapOf(
                    "street" to user.address.street,
                    "number" to user.address.number,
                    "neighborhood" to user.address.neighborhood,
                    "city" to user.address.city,
                    "state" to user.address.state,
                    "postalCode" to user.address.postalCode
                )
            )

            // Salva os dados do usuário no Firestore
            firestore.collection("users").document(userId).set(userData).await()
            Result.success(userId)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("O email já está registrado. Por favor, faça login ou use outro email."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserInfo(
        userId: String,
        updatedUser: User,
        address: Address
    ): Result<Unit> {
        return try {
            val userData = mutableMapOf<String, Any>(
                "username" to updatedUser.username,
                "email" to updatedUser.email,
                "address" to hashMapOf(
                    "street" to address.street,
                    "number" to address.number,
                    "neighborhood" to address.neighborhood,
                    "city" to address.city,
                    "state" to address.state,
                    "postalCode" to address.postalCode
                )
            )
            firestore.collection("users").document(userId).update(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val documentSnapshot = firestore.collection("users").document(userId).get().await()
            if (documentSnapshot.exists()) {
                val username = documentSnapshot.getString("username") ?: ""
                val email = documentSnapshot.getString("email") ?: ""
                val addressData = documentSnapshot.get("address") as? Map<String, String>

                val address = Address(
                    street = addressData?.get("street") ?: "",
                    number = addressData?.get("number") ?: "",
                    neighborhood = addressData?.get("neighborhood") ?: "",
                    city = addressData?.get("city") ?: "",
                    state = addressData?.get("state") ?: "",
                    postalCode = addressData?.get("postalCode") ?: ""
                )

                val user = User(
                    id = userId,
                    username = username,
                    email = email,
                    passwordHash = "", // O hash da senha não é recuperado por questões de segurança
                    address = address  // Passa o objeto Address completo
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Usuário não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
