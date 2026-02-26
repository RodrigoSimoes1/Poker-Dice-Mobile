package com.example.myapplication.lobbies.http

import com.example.myapplication.commonElements.HttpPaths
import com.example.myapplication.error.ApiErrorResponse
import com.example.myapplication.error.ApiException
import com.example.myapplication.lobbies.domain.LobbyService
import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay

class LobbyServiceHttp(
    private val client: HttpClient,
) : LobbyService {
    private suspend fun validateResponse(response: HttpResponse): HttpResponse {
        if (!response.status.isSuccess()) {
            val errorBody =
                try {
                    response.body<ApiErrorResponse>()
                } catch (_: Exception) {
                    ApiErrorResponse(
                        title = "Erro no Servidor (${response.status.value})",
                        description = "Ocorreu um problema técnico ou formato inválido.",
                        solution = "Tente novamente.",
                    )
                }
            throw ApiException(errorBody)
        }
        return response
    }

    override suspend fun getAllAvailableLobbies(token: String) =
        runCatching {
            delay(1000)
            val response =
                client.get(HttpPaths.LOBBY_PATH) {
                    bearerAuth(token)
                }
            validateResponse(response).body<List<LobbyOutputModel>>()
        }

    override suspend fun getLobbyById(
        token: String,
        lobbyId: Int,
    ) = runCatching {
        val response =
            client.get("${HttpPaths.LOBBY_PATH}/$lobbyId") {
                bearerAuth(token)
            }
        validateResponse(response).body<LobbyOutputModel>()
    }

    private suspend fun postLobbyAction(
        token: String,
        path: String,
    ): LobbyOutputModel {
        val response =
            client.post(path) {
                bearerAuth(token)
            }
        return validateResponse(response).body<LobbyOutputModel>()
    }

    override suspend fun joinLobby(
        token: String,
        lobbyId: Int,
    ) = runCatching {
        postLobbyAction(token, "${HttpPaths.LOBBY_PATH}/$lobbyId/join")
    }

    override suspend fun leaveLobby(
        token: String,
        lobbyId: Int,
    ) = runCatching {
        postLobbyAction(token, "${HttpPaths.LOBBY_PATH}/$lobbyId/leave")
    }

    override suspend fun createLobby(
        token: String,
        lobby: CreateLobbyInputModel,
    ) = runCatching {
        val response =
            client.post(HttpPaths.LOBBY_PATH) {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(lobby)
            }
        validateResponse(response)
        val locationHeader =
            response.headers[HttpHeaders.Location]!!

        val id = locationHeader.substringAfterLast("/").toInt()
        getLobbyById(token, id).getOrThrow()
    }

    override suspend fun getLobbyByUserId(
        authToken: String,
        userId: Int,
    ): Result<LobbyOutputModel> =
        runCatching {
            val url = HttpPaths.LOBBY_BY_USER.replace("{userId}", userId.toString())

            val response =
                client.get(url) {
                    bearerAuth(authToken)
                }

            validateResponse(response).body<LobbyOutputModel>()
        }
}
