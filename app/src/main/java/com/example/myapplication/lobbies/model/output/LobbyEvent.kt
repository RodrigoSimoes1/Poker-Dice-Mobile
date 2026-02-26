package com.example.myapplication.lobbies.model.output

import kotlinx.serialization.Serializable

sealed class LobbyEvent {
    data class AddOrUpdate(
        val lobby: LobbyOutputModel,
    ) : LobbyEvent()

    data class Remove(
        val lobbyId: Int,
    ) : LobbyEvent()

    data class PlayerJoined(
        val userId: Int,
        val name: String,
        val email: String,
    ) : LobbyEvent()

    data class PlayerLeft(
        val userId: Int,
    ) : LobbyEvent()

    data class LeaderLeft(
        val reason: String,
    ) : LobbyEvent()

    data class MatchStarted(
        val matchId: Int,
    ) : LobbyEvent()
}

@Serializable
data class RemovePayload(
    val id: Int,
)

@Serializable
data class PlayerInOutPayload(
    val userId: Int,
    val username: String,
    val email: String,
)

@Serializable
data class LeaderLeftPayload(
    val userId: Int,
    val reason: String,
)

@Serializable
data class MatchCreatedPayload(
    val id: Int,
)
