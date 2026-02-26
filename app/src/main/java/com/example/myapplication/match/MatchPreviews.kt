package com.example.myapplication.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.domainlogic.Dice
import com.example.myapplication.domainlogic.DiceFace
import com.example.myapplication.domainlogic.Hand
import com.example.myapplication.domainlogic.HandRank
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.MatchStatusOutputModel
import com.example.myapplication.match.model.output.PlayerOutputModel

private val mockDiceList =
    listOf(
        Dice(1, DiceFace.ACE),
        Dice(2, DiceFace.KING),
        Dice(3, DiceFace.TEN),
        Dice(4, DiceFace.JACK),
        Dice(5, DiceFace.QUEEN),
    )

private val mockPlayers =
    listOf(
        PlayerOutputModel(1, "EU", "hero@test.com", 1500, null, 10, 5),
        PlayerOutputModel(2, "player 1", "v1@test.com", 5000, null, 20, 15),
        PlayerOutputModel(3, "player 2", "v2@test.com", 200, null, 5, 0),
        PlayerOutputModel(4, "player 3", "v3@test.com", 800, null, 8, 2),
    )

private val mockMatchInfo =
    MatchStatusOutputModel(
        id = 100,
        lobbyId = 50,
        players = mockPlayers,
        isFinished = false,
    )

private val mockTurnMyTurn =
    CurrentTurnOutputModel(
        userId = 1,
        userName = "Eu",
        pointsEarned = 0,
        dices = mockDiceList,
        handRank = HandRank.STRAIGHT,
        triesLeft = 2,
    )

private val mockTurnOpponent =
    CurrentTurnOutputModel(
        userId = 2,
        userName = "Player 1",
        pointsEarned = 50,
        dices = mockDiceList,
        handRank = HandRank.ONE_PAIR,
        triesLeft = 1,
    )
private val mockHandFullHouse =
    Hand(
        dices =
            listOf(
                Dice(1, DiceFace.ACE),
                Dice(2, DiceFace.ACE),
                Dice(3, DiceFace.ACE),
                Dice(4, DiceFace.KING),
                Dice(5, DiceFace.KING),
            ),
        handRank = HandRank.FULL_HOUSE,
    )

private val mockPlayersResults =
    listOf(
        PlayerOutputModel(1, "Eu", "hero@test", 500, mockHandFullHouse, 1, 1),
        PlayerOutputModel(
            2,
            "Jogador 2",
            "p2@test",
            120,
            mockHandFullHouse.copy(handRank = HandRank.TWO_PAIR),
            1,
            0,
        ),
        PlayerOutputModel(3, "Jogador 3", "p3@test", 0, null, 1, 0),
    )

@Preview(showBackground = true, name = "Round Ended Overlay")
@Composable
fun RoundResultPreview() {
    Box(Modifier.fillMaxSize().background(Color(0xFF2E7D32))) {
        RoundResultOverlay(
            players = mockPlayersResults,
            countdown = 3,
        )
    }
}

@Preview(showBackground = true, name = "Match Finished Winner")
@Composable
fun MatchFinishedPreview() {
    MatchFinishedView(
        winner = mockPlayersResults[0],
        countdown = 8,
    )
}

@Preview(showBackground = true, name = "1. Minha Vez (Com Reroll)")
@Composable
fun MatchViewMyTurnPreview() {
    val state =
        MatchState.Playing(
            lobbyId = 1,
            matchInfo = mockMatchInfo,
            currentTurn = mockTurnMyTurn,
            isMyTurn = true,
            dicesToKeep = listOf(true, false, false, true, true),
            isRolling = false,
        )

    MaterialTheme {
        MatchView(
            state = state,
            onRoll = {},
            onAccept = {},
            onToggleDice = {},
        )
    }
}

@Preview(showBackground = true, name = "2. Vez do Oponente")
@Composable
fun MatchViewOpponentTurnPreview() {
    val state =
        MatchState.Playing(
            lobbyId = 1,
            matchInfo = mockMatchInfo,
            currentTurn = mockTurnOpponent,
            isMyTurn = false,
            dicesToKeep = listOf(true, true, true, true, true),
            isRolling = true,
        )

    MaterialTheme {
        MatchView(
            state = state,
            onRoll = {},
            onAccept = {},
            onToggleDice = {},
        )
    }
}

@Preview(showBackground = true, name = "3. Jogo Não Começado")
@Composable
fun MatchViewStartPreview() {
    val state =
        MatchState.Playing(
            lobbyId = 1,
            matchInfo = mockMatchInfo,
            currentTurn = null,
            isMyTurn = false,
        )

    MaterialTheme {
        MatchView(
            state = state,
            onRoll = {},
            onAccept = {},
            onToggleDice = {},
        )
    }
}
