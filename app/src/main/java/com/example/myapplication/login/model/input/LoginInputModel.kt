package com.example.myapplication.login.model.input

import kotlinx.serialization.Serializable

@Serializable
data class LoginInputModel(
    val email: String,
    val password: String,
)
