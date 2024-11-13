package com.example.ubkk3.firebaseSignIn

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val email: String,
    val username: String?,
    val profilePictureUrl: String?,
    val isAdmin: Boolean
)