package com.example.myapplication.match.model.output

import kotlinx.serialization.Serializable

@Serializable
data class MatchStatusOutputModel(
    val id: Int,
    val lobbyId: Int,
    val players: List<PlayerOutputModel>,
    val isFinished: Boolean,
)
