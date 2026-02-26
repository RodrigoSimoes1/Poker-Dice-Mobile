package com.example.myapplication.lobbies.model.output

import kotlinx.serialization.Serializable

@Serializable
data class LobbyOutputModel(
    val id: Int,
    val name: String,
    val description: String,
    val host: UserInLobbyOutputModel,
    val players: List<UserInLobbyOutputModel>,
    val isPublic: Boolean,
    val maxPlayers: Int,
    val minPlayers: Int,
    val numberOfRounds: Int,
    val costToPlay: Int = 0,
)
