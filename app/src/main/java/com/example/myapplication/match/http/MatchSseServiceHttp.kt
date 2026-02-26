package com.example.myapplication.match.http

import com.example.myapplication.commonElements.HttpPaths
import com.example.myapplication.match.domain.MatchSseService
import com.example.myapplication.match.model.output.MatchEvent
import com.example.myapplication.match.model.output.MatchSsePayload
import com.example.myapplication.match.model.output.WinnerPayload
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class MatchSseServiceHttp(
    private val client: HttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : MatchSseService {
    override suspend fun subscribeToMatch(matchId: Int): Flow<MatchEvent> =
        flow {
            try {
                val url = HttpPaths.SSE_MATCH.replace("{matchId}", matchId.toString())

                client.sse(
                    urlString = url,
                    request = {
                    },
                ) {
                    incoming.collect { event ->
                        val type = event.event?.trim()
                        val data = event.data

                        if (!data.isNullOrBlank()) {
                            try {
                                when (type) {
                                    "MATCH_CREATED", "MATCH_ROLL", "TURN_CHANGE" -> {
                                        val payload = json.decodeFromString<MatchSsePayload>(data)
                                        emit(MatchEvent.MatchUpdate(payload))
                                    }

                                    "MATCH_ROUND_END" -> {
                                        val payload = json.decodeFromString<MatchSsePayload>(data)
                                        emit(MatchEvent.RoundEnd(payload))
                                    }
                                    "MATCH_WINNER" -> {
                                        val payload = json.decodeFromString<WinnerPayload>(data)
                                        emit(MatchEvent.MatchWinner(payload))
                                    }
                                }
                            } catch (e: Exception) {
                                emit(MatchEvent.Error("Failed to parse SSE data: ${e.message}"))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                emit(MatchEvent.Error("Connection error: ${e.message}"))
            }
        }
}