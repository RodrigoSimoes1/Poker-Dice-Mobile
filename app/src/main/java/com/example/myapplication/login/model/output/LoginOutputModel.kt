package com.example.myapplication.login.model.output

import kotlinx.serialization.Serializable

@Serializable
data class LoginOutputModel(
    val token: String,
)
