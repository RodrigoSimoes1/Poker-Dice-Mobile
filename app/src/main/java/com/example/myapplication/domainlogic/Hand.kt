package com.example.myapplication.domainlogic

import kotlinx.serialization.Serializable

@Serializable
data class Hand(
    val dices: List<Dice>,
    val handRank: HandRank,
)

@Serializable
enum class HandRank {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    STRAIGHT,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    BUST,
}

fun getFaceValue(face: DiceFace): Int =
    when (face) {
        DiceFace.ACE -> 14
        DiceFace.KING -> 13
        DiceFace.QUEEN -> 12
        DiceFace.JACK -> 11
        DiceFace.TEN -> 10
        DiceFace.NINE -> 9
    }

/**
 * Obtém o bónus baseado na combinação (Bonus Score).
 */
fun getRankBonus(rank: HandRank?): Int =
    when (rank) {
        HandRank.FIVE_OF_A_KIND -> 60
        HandRank.FOUR_OF_A_KIND -> 50
        HandRank.FULL_HOUSE -> 40
        HandRank.STRAIGHT -> 30
        HandRank.THREE_OF_A_KIND -> 20
        HandRank.TWO_PAIR -> 10
        HandRank.ONE_PAIR -> 0
        HandRank.BUST -> 0
        null -> 0
    }

/**
 * Calcula a pontuação total: Soma dos dados + Bónus do Rank.
 */
fun calculateTotalScore(
    dices: List<Dice>?,
    rank: HandRank?,
): Int {
    if (dices == null || rank == null) return 0

    val baseScore = dices.sumOf { getFaceValue(it.face) }

    val bonusScore = getRankBonus(rank)

    return baseScore + bonusScore
}
