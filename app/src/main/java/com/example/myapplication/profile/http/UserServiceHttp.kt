package com.example.myapplication.profile.http

import com.example.myapplication.commonElements.HttpPaths
import com.example.myapplication.error.ApiErrorResponse
import com.example.myapplication.error.ApiException
import com.example.myapplication.profile.domain.UserService
import com.example.myapplication.profile.model.InviteOutputModel
import com.example.myapplication.profile.model.UserOutputModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class UserServiceHttp(
    private val client: HttpClient,
) : UserService {
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

    override suspend fun getAllUsers() =
        runCatching {
            val response =
                client.get(HttpPaths.USERS_PATH) {
                }
            validateResponse(response).body<List<UserOutputModel>>()
        }

    override suspend fun getUserById(userId: Int) =
        runCatching {
            val response =
                client.get("${HttpPaths.USERS_PATH}/$userId") {
                }
            validateResponse(response).body<UserOutputModel>()
        }

    override suspend fun createInvite(token: String): Result<InviteOutputModel> =
        runCatching {
            val response =
                client.post(HttpPaths.INVITE_PATH) {
                    bearerAuth(token)
                }
            validateResponse(response).body<InviteOutputModel>()
        }
}
