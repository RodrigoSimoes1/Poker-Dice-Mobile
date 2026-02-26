package com.example.myapplication.lobbies.http

import com.example.myapplication.commonElements.HttpPaths
import com.example.myapplication.lobbies.domain.LobbySseService
import com.example.myapplication.lobbies.model.output.LeaderLeftPayload
import com.example.myapplication.lobbies.model.output.LobbyEvent
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.lobbies.model.output.MatchCreatedPayload
import com.example.myapplication.lobbies.model.output.PlayerInOutPayload
import com.example.myapplication.lobbies.model.output.RemovePayload
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.client.plugins.timeout
import io.ktor.client.request.bearerAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class LobbySseServiceHttp(
    private val client: HttpClient,
) : LobbySseService {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = true
        }

    override suspend fun subscribeGlobalLobbies(token: String): Flow<LobbyEvent> =
        flow {
            try {
                client.sse(
                    urlString = HttpPaths.SSE_LOBBYS,
                    request = {
                        bearerAuth(token)
                        timeout {
                            requestTimeoutMillis = Long.MAX_VALUE
                            socketTimeoutMillis = Long.MAX_VALUE
                            connectTimeoutMillis = 10000
                        }
                    },
                ) {
                    incoming.collect { event ->
                        val eventType = event.event?.trim()
                        val data = event.data

                        if (!data.isNullOrBlank()) {
                            when (eventType) {
                                "LOBBY_ADDED", "LOBBY_UPDATED" -> {
                                    val lobby = jsonParser.decodeFromString<LobbyOutputModel>(data)
                                    emit(LobbyEvent.AddOrUpdate(lobby))
                                }

                                "LOBBY_REMOVED" -> {
                                    val payload = jsonParser.decodeFromString<RemovePayload>(data)
                                    emit(LobbyEvent.Remove(payload.id))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("SSE Connection Error: ${e.message}")
            }
        }

    override suspend fun subscribeLobbyState(
        token: String,
        lobbyId: Int,
    ): Flow<LobbyEvent> =
        flow {
            try {
                client.sse(
                    urlString = "${HttpPaths.SSE_LOBBY}/$lobbyId",
                    request = {
                        bearerAuth(token)
                        timeout {
                            requestTimeoutMillis = Long.MAX_VALUE
                            socketTimeoutMillis = Long.MAX_VALUE
                            connectTimeoutMillis = 10000
                        }
                    },
                ) {
                    incoming.collect { event ->
                        val eventType = event.event?.trim()
                        val data = event.data

                        if (!data.isNullOrBlank()) {
                            when (eventType) {
                                "LOBBY_PLAYER_JOINED" -> {
                                    try {
                                        val payload = jsonParser.decodeFromString<PlayerInOutPayload>(data)

                                        emit(
                                            LobbyEvent.PlayerJoined(
                                                userId = payload.userId,
                                                name = payload.username,
                                                email = payload.email,
                                            ),
                                        )
                                    } catch (e: Exception) {
                                        println("SSE Parse Error (Join): ${e.message}")
                                    }
                                }

                                "LOBBY_PLAYER_LEFT" -> {
                                    try {
                                        val payload = jsonParser.decodeFromString<PlayerInOutPayload>(data)
                                        emit(LobbyEvent.PlayerLeft(payload.userId))
                                    } catch (e: Exception) {
                                        println("SSE Parse Error (Left): ${e.message}")
                                    }
                                }
                                "LOBBY_LEADER_LEFT" -> {
                                    try {
                                        val payload = jsonParser.decodeFromString<LeaderLeftPayload>(data)
                                        emit(LobbyEvent.LeaderLeft(payload.reason))
                                    } catch (_: Exception) {
                                        emit(LobbyEvent.LeaderLeft("Host disconnected"))
                                    }
                                }

                                "LOBBY_UPDATED", "GAME_STARTED" -> {
                                    try {
                                        val lobby = jsonParser.decodeFromString<LobbyOutputModel>(data)
                                        emit(LobbyEvent.AddOrUpdate(lobby))
                                    } catch (_: Exception) {
                                    }
                                }
                                "MATCH_CREATED" -> {
                                    try {
                                        val payload = jsonParser.decodeFromString<MatchCreatedPayload>(data)
                                        emit(LobbyEvent.MatchStarted(payload.id))
                                    } catch (e: Exception) {
                                        println("Erro parse MatchCreated: ${e.message}")
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("SSE Connection Error: ${e.message}")
            }
        }
}
