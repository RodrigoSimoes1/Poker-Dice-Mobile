package com.example.myapplication.match.http

import com.example.myapplication.commonElements.HttpPaths
import com.example.myapplication.error.ApiErrorResponse
import com.example.myapplication.error.ApiException
import com.example.myapplication.match.domain.MatchService
import com.example.myapplication.match.model.input.RollDiceInputModel
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.MatchOutputModel
import com.example.myapplication.match.model.output.MatchStatusOutputModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class MatchServiceHttp(
    private val client: HttpClient,
) : MatchService {
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

    override suspend fun getMatchStatus(
        token: String,
        lobbyId: Int,
    ): Result<MatchStatusOutputModel> =
        runCatching {
            val url = HttpPaths.STATUS_PATH.replace("{lobbiesId}", lobbyId.toString())

            val response =
                client.get(url) {
                    bearerAuth(token)
                }
            validateResponse(response).body<MatchStatusOutputModel>()
        }

    override suspend fun startMatch(
        token: String,
        lobbyId: Int,
    ): Result<MatchOutputModel> =
        runCatching {
            val url = HttpPaths.START_PATH.replace("{lobbiesId}", lobbyId.toString())

            val response =
                client.post(url) {
                    bearerAuth(token)
                }
            validateResponse(response).body<MatchOutputModel>()
        }

    override suspend fun rollDice(
        token: String,
        lobbyId: Int,
        input: RollDiceInputModel,
    ): Result<CurrentTurnOutputModel> =
        runCatching {
            val url = HttpPaths.ROLL_PATH.replace("{lobbiesId}", lobbyId.toString())

            val response =
                client.post(url) {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(input)
                }
            validateResponse(response).body<CurrentTurnOutputModel>()
        }

    override suspend fun acceptPlay(
        token: String,
        lobbyId: Int,
    ): Result<MatchOutputModel> =
        runCatching {
            val url = HttpPaths.ACCEPT_PATH.replace("{lobbiesId}", lobbyId.toString())

            val response =
                client.post(url) {
                    bearerAuth(token)
                }
            validateResponse(response).body<MatchOutputModel>()
        }
}
