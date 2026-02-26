package com.example.myapplication.lobbies.model.input

import kotlinx.serialization.Serializable

@Serializable
data class CreateLobbyInputModel(
    val name: String,
    val description: String,
    val maxPlayers: Int,
    val minPlayers: Int,
    val numberOfRounds: Int,
    val costToPlay: Int,
)
