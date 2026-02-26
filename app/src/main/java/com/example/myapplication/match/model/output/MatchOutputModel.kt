package com.example.myapplication.match.model.output

import com.example.myapplication.profile.model.UserOutputModel
import kotlinx.serialization.Serializable

@Serializable
data class MatchOutputModel(
    val id: Int,
    val lobbyId: Int,
    val players: List<UserOutputModel>,
    val isFinished: Boolean,
)
