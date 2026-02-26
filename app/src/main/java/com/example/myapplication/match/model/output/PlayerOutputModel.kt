package com.example.myapplication.match.model.output

import com.example.myapplication.domainlogic.Hand
import kotlinx.serialization.Serializable

@Serializable
data class PlayerOutputModel(
    val id: Int,
    val name: String,
    val email: String,
    val balance: Int,
    val playerHand: Hand?,
    val gamesPlayed: Int,
    val gamesWon: Int,
)
