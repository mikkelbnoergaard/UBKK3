package com.example.ubkk3.firebaseSignIn

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isAdmin: Boolean = false
)
