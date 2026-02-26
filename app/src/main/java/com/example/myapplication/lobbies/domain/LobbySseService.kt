package com.example.myapplication.lobbies.domain

import com.example.myapplication.lobbies.model.output.LobbyEvent
import kotlinx.coroutines.flow.Flow

interface LobbySseService {
    suspend fun subscribeGlobalLobbies(token: String): Flow<LobbyEvent>

    suspend fun subscribeLobbyState(
        token: String,
        lobbyId: Int,
    ): Flow<LobbyEvent>
}
