package com.example.myapplication.login.model.input

import kotlinx.serialization.Serializable

@Serializable
data class RegisterInputModel(
    val name: String,
    val email: String,
    val password: String,
    val inviteCode: String,
)
