package com.example.myapplication.match.model.input

import kotlinx.serialization.Serializable

@Serializable
data class RollDiceInputModel(
    val dice1: String? = null,
    val dice2: String? = null,
    val dice3: String? = null,
    val dice4: String? = null,
    val dice5: String? = null,
)
