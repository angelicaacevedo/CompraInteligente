package br.com.angelica.comprainteligente.data.repository.auth


import br.com.angelica.comprainteligente.model.Address
import br.com.angelica.comprainteligente.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun register(user: User, address: Address): Result<Unit> {
        return try {
            // Register the user in Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(user.email, user.passwordHash).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")

            // Save the user and address in Firestore
            val userData = hashMapOf(
                "id" to userId,
                "username" to user.username,
                "email" to user.email,
                "address" to hashMapOf(
                    "street" to address.street,
                    "number" to address.number,
                    "neighborhood" to address.neighborhood,
                    "city" to address.city,
                    "state" to address.state,
                    "postalCode" to address.postalCode
                )
            )
            firestore.collection("users").document(userId).set(userData).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}