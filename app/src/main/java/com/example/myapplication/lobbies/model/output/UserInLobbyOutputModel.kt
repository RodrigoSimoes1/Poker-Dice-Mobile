package com.example.myapplication.lobbies.model.output

import kotlinx.serialization.Serializable

@Serializable
data class UserInLobbyOutputModel(
    val id: Int,
    val name: String,
    val email: String,
)
