package com.example.myapplication.profile.domain

import com.example.myapplication.profile.model.InviteOutputModel
import com.example.myapplication.profile.model.UserOutputModel

interface UserService {
    suspend fun getAllUsers(): Result<List<UserOutputModel>>

    suspend fun getUserById(userId: Int): Result<UserOutputModel>

    suspend fun createInvite(token: String): Result<InviteOutputModel>
}
