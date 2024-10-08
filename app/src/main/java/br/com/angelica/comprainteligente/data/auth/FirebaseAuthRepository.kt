package br.com.angelica.comprainteligente.data.auth

import br.com.angelica.comprainteligente.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return executeAuthOperation {
            auth.signInWithEmailAndPassword(email, password).await().user
        }
    }

    override suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return executeAuthOperation {
            auth.createUserWithEmailAndPassword(email, password).await().user
        }
    }

    override suspend fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val document = db.collection("users").document(userId).get().await()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): Result<UserProfile> {
        return try {
            db.collection("users").document(userProfile.userId).set(userProfile).await()
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun executeAuthOperation(operation: suspend () -> FirebaseUser?): Result<FirebaseUser> {
        return try {
            val user = operation()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Autenticação falhou"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}