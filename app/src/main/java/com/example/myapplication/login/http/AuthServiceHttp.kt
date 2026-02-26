package com.example.myapplication.login.http

import com.example.myapplication.commonElements.HttpPaths
import com.example.myapplication.error.ApiErrorResponse
import com.example.myapplication.error.ApiException
import com.example.myapplication.login.domain.AuthService
import com.example.myapplication.login.model.input.LoginInputModel
import com.example.myapplication.login.model.input.RegisterInputModel
import com.example.myapplication.login.model.output.LoginOutputModel
import com.example.myapplication.profile.model.UserOutputModel
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

class AuthServiceHttp(
    private val client: HttpClient,
) : AuthService {
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

    private suspend fun post(
        path: String,
        body: Any,
    ): HttpResponse {
        val response =
            client.post(path) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        return validateResponse(response)
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        inviteCode: String,
    ) = runCatching {
        post(HttpPaths.REGISTER_PATH, RegisterInputModel(name, email, password, inviteCode))
        Unit
    }

    override suspend fun login(
        email: String,
        password: String,
    ) = runCatching {
        val response = post(HttpPaths.LOGIN_PATH, LoginInputModel(email, password))
        response.body<LoginOutputModel>()
    }

    override suspend fun logout(token: String) =
        runCatching {
            val response =
                client.post(HttpPaths.LOGOUT_PATH) {
                    bearerAuth(token)
                }
            validateResponse(response)
            Unit
        }

    override suspend fun fetchMe(token: String): Result<UserOutputModel> =
        runCatching {
            val response =
                client.get(HttpPaths.ME_PATH) {
                    bearerAuth(token)
                }
            validateResponse(response).body<UserOutputModel>()
        }
}
