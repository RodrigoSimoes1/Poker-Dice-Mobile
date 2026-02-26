package com.example.myapplication.match.domain

import com.example.myapplication.match.model.input.RollDiceInputModel
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.MatchOutputModel
import com.example.myapplication.match.model.output.MatchStatusOutputModel

interface MatchService {
    suspend fun getMatchStatus(
        token: String,
        lobbyId: Int,
    ): Result<MatchStatusOutputModel>

    suspend fun startMatch(
        token: String,
        lobbyId: Int,
    ): Result<MatchOutputModel>

    suspend fun rollDice(
        token: String,
        lobbyId: Int,
        input: RollDiceInputModel,
    ): Result<CurrentTurnOutputModel>

    suspend fun acceptPlay(
        token: String,
        lobbyId: Int,
    ): Result<MatchOutputModel>
}
