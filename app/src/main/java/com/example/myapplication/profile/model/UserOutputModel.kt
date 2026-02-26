package com.example.myapplication.profile.model

import kotlinx.serialization.Serializable

@Serializable
data class UserOutputModel(
    val id: Int,
    val name: String,
    val email: String,
    val balance: Int,
    val gamesPlayed: Int,
    val gamesWon: Int,
)
