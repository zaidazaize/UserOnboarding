package com.example.useronboarding.login

import android.app.Activity
import com.example.useronboarding.login.model.Result
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */


class LoginRegisterRepository @Inject constructor(
    private val dataSource: LoginRegisterDataSource,
) {

    // in-memory cache of the loggedInUser object
    private var user: LoggedInUser? = null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        user = null
    }

    // Due to structured concurrency once the viewModelScope is cancelled all the coroutines
    // launched in the scope will be cancelled. So, we need not to worry about the cancellation and
    // memory leaks.
    @OptIn(ExperimentalCoroutinesApi::class)
    fun login(username: String, password: String): Flow<Result<LoggedInUser>> = flow {
        val loginResult = dataSource.login(username, password)
        // we are keeping the user data in the memory for now.
        // since we are using firebase auth we can get the user data from the auth object
        emit(loginResult)
    }.onStart {
        emit(Result.Loading(true))
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun register(username: String, password: String): Flow<Result<LoggedInUser>> =
        dataSource.register(username, password).mapLatest { result ->
            if (result is Result.Success) {
                setLoggedInUser(result.data)
            }
            result
        }.flowOn(Dispatchers.IO)

    fun logout() {
        user = null
        dataSource.logout()
    }

    // this function will be more usable if we use our own server for authentication
    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    fun loginWithGoogle(idToken: String) =
        dataSource.signinWithGoogle(idToken = idToken).flowOn(Dispatchers.IO)

    lateinit var omVerificationCode:String

    fun createUserWithPhone(phone: String,activity:Activity): Flow<Result<String>> =  callbackFlow{
        trySend(Result.Loading(true))

        val onVerificationCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                trySend(Result.Error(p0))
            }

            override fun onCodeSent(verificationCode: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationCode, p1)
                trySend(Result.Success("OTP Sent Successfully"))
                omVerificationCode = verificationCode
            }

        }

        val options = PhoneAuthOptions.newBuilder(dataSource.auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(onVerificationCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose{
            close()
        }
    }
    .onStart {
        emit(Result.Loading(true))
    }.flowOn(Dispatchers.IO)

     fun signWithCredential(otp: String): Flow<Result<String>>  = callbackFlow{
        trySend(Result.Loading(true))
        val credential = PhoneAuthProvider.getCredential(omVerificationCode,otp)
        dataSource.auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful)
                    trySend(Result.Success("otp verified"))
            }.addOnFailureListener {
                trySend(Result.Error(it))
            }
        awaitClose {
            close()
        }
    }

}