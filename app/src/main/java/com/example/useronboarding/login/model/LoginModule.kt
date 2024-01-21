package com.example.useronboarding.login.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class LoginModule{

    @Singleton
    @Provides
    fun provideFirebaseAuth() :FirebaseAuth{
        return Firebase.auth
    }
}