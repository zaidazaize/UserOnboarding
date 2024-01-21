package com.example.useronboarding.login.model

data class LoggedInUserView(
    val displayName: String,
    val email: String? = null,
    val password: String? = null,
    //... other data fields that may be accessible to the UI
)