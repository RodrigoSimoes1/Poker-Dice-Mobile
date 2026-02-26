package com.example.myapplication.shop.http

import com.example.myapplication.commonElements.HttpPaths
import com.example.myapplication.error.ApiErrorResponse
import com.example.myapplication.error.ApiException
import com.example.myapplication.profile.model.UserOutputModel
import com.example.myapplication.shop.domain.BalanceService
import com.example.myapplication.shop.model.BalancePurchaseInputModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class BalanceServiceHttp(
    private val client: HttpClient,
) : BalanceService {
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

    override suspend fun addBalance(
        token: String,
        code: BalancePurchaseInputModel,
    ): Result<UserOutputModel> =
        runCatching {
            val response =
                client.post(HttpPaths.BALANCE_PATH) {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                    setBody(code)
                }
            validateResponse(response).body<UserOutputModel>()
        }
}
