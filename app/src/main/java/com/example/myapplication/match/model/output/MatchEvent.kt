package com.example.myapplication.match.model.output

import kotlinx.serialization.Serializable

sealed class MatchEvent {
    data class MatchUpdate(
        val match: MatchSsePayload,
    ) : MatchEvent()

    data class RoundEnd(
        val match: MatchSsePayload,
    ) : MatchEvent()

    data class MatchWinner(
        val winnerInfo: WinnerPayload,
    ) : MatchEvent()

    data class Error(
        val message: String,
    ) : MatchEvent()
}

@Serializable
data class MatchSsePayload(
    val id: Int,
    val hostId: Int,
    val players: List<PlayerSsePayload>,
    val isFinished: Boolean,
    val currentTurnPlayerId: Int?,
    val rollsLeft: Int,
    val round: Int,
    val status: String,
    val winner: PlayerSsePayload? = null,
)

@Serializable
data class PlayerSsePayload(
    val user: UserSsePayload,
    val points: Int,
    val playerHand: HandSsePayload,
)

@Serializable
data class UserSsePayload(
    val id: Int,
    val name: String,
    val email: String,
    val gamesWon: Int,
    val balance: Int,
    val gamesPlayed: Int,
)

@Serializable
data class HandSsePayload(
    val dices: List<DiceSsePayload>,
    val rank: String,
)

@Serializable
data class DiceSsePayload(
    val face: String,
)

@Serializable
data class WinnerPayload(
    val winnerId: Int,
    val winnerName: String,
    val winnings: Int,
)
