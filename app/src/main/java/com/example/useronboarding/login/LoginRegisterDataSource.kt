package com.example.useronboarding.login

import com.example.useronboarding.login.model.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Class that handles authentication and retrieves user information. Manage login and registration.
 *
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String,
)
class LoginRegisterDataSource @Inject constructor(
    val auth: FirebaseAuth
) {

    suspend fun login(email: String, password: String): Result<LoggedInUser>  {
        val result = try {
            val user = auth.signInWithEmailAndPassword(email, password).await()
            if (user.user != null) {
                Result.Success(
                    LoggedInUser(
                        auth.currentUser?.uid.toString(),
                        auth.currentUser?.email.toString()
                    )
                )
            } else {
                Result.Error(NullPointerException("No user found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
        return result
    }

    fun register(username: String, password: String): Flow<Result<LoggedInUser>> = flow {
        val result = try {
            val user = auth.createUserWithEmailAndPassword(username, password).await()

            if (user.user != null) {
                Result.Success(LoggedInUser(user.user?.uid.toString(), user.user?.email.toString()))
            } else {
                Result.Error(Exception("No user found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
        emit(result)
    }

    fun signinWithGoogle(idToken:String): Flow<Result<LoggedInUser>> = flow{
        val result = try {
            val credential = auth.signInWithCredential(
                GoogleAuthProvider.getCredential(idToken, null)
            ).await()
            if (credential.user != null) {
                Result.Success(
                    LoggedInUser(
                        auth.currentUser?.uid.toString(),
                        auth.currentUser?.email.toString()
                    )
                )
            } else {
                Result.Error(NullPointerException("No user found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    fun logout() {
        auth.signOut()
    }

}
