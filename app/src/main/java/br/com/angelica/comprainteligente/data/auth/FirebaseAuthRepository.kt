package br.com.angelica.comprainteligente.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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