package com.example.useronboarding

import android.app.Application
import com.example.useronboarding.login.LoginRegisterDataSource
import com.example.useronboarding.login.LoginRegisterRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MyApplication  : Application(){

}

object Dependency {
    val firebaseAuth = Firebase.auth

    fun getLoginRepository() : LoginRegisterRepository{
        return LoginRegisterRepository(
            dataSource = LoginRegisterDataSource(
                auth = firebaseAuth
            )
        )
    }
}