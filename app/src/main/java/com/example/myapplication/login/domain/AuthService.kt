package com.example.myapplication.login.domain

import com.example.myapplication.login.model.output.LoginOutputModel
import com.example.myapplication.profile.model.UserOutputModel

interface AuthService {
    suspend fun register(
        name: String,
        email: String,
        password: String,
        inviteCode: String,
    ): Result<Unit>

    suspend fun login(
        email: String,
        password: String,
    ): Result<LoginOutputModel>

    suspend fun logout(token: String): Result<Unit>

    suspend fun fetchMe(token: String): Result<UserOutputModel>
}
