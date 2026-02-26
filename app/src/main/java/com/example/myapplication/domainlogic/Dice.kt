package com.example.myapplication.domainlogic

import kotlinx.serialization.Serializable

@Serializable
data class Dice(
    val id: Int,
    val face: DiceFace,
)

@Serializable
enum class DiceFace {
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE,
}

enum class DiceAction(
    val value: String,
) {
    KEEP("KEEP"),
    CHANGE("CHANGE"),
}
