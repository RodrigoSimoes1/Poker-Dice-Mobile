package com.example.myapplication.match.model.output

import com.example.myapplication.domainlogic.Dice
import com.example.myapplication.domainlogic.HandRank
import kotlinx.serialization.Serializable

@Serializable
data class CurrentTurnOutputModel(
    val userId: Int,
    val userName: String,
    val pointsEarned: Int,
    val dices: List<Dice>,
    val handRank: HandRank,
    val triesLeft: Int,
)
