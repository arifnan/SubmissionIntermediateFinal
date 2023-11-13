package com.example.submissionintermediate.auth

data class UserModel (
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)