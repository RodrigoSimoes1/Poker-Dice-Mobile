package com.example.myapplication.lobbies.domain

import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import com.example.myapplication.lobbies.model.output.LobbyOutputModel

interface LobbyService {
    suspend fun getAllAvailableLobbies(token: String): Result<List<LobbyOutputModel>>

    suspend fun getLobbyById(
        token: String,
        lobbyId: Int,
    ): Result<LobbyOutputModel>

    suspend fun joinLobby(
        token: String,
        lobbyId: Int,
    ): Result<LobbyOutputModel>

    suspend fun leaveLobby(
        token: String,
        lobbyId: Int,
    ): Result<LobbyOutputModel>

    suspend fun createLobby(
        token: String,
        lobby: CreateLobbyInputModel,
    ): Result<LobbyOutputModel>

    suspend fun getLobbyByUserId(
        authToken: String,
        userId: Int,
    ): Result<LobbyOutputModel>
}
