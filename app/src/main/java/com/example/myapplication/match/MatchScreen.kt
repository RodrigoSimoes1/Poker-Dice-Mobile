package com.example.myapplication.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

const val LOADING_STATE_TAG = "LOADING_STATE_TAG"
const val ERROR_STATE_TAG = "ERROR_STATE_TAG"
const val MATCH_PLAYING_TAG = "MATCH_PLAYING_TAG"
const val STATE_MATCH_ROUND_ENDED = "STATE_MATCH_ROUND_ENDED"
const val MATCH_FINISHED_VIEW = "MATCH_FINISHED_VIEW"


enum class MatchNavigationIntent {
    BackToLobbies,
}

@Composable
fun MatchScreen(
    viewModel: MatchViewModel,
    onNavigateBack: (MatchNavigationIntent) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        MatchState.Loading -> Box(Modifier.fillMaxSize().testTag(LOADING_STATE_TAG), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is MatchState.Error -> Box(Modifier.fillMaxSize().testTag(ERROR_STATE_TAG), contentAlignment = Alignment.Center) { Text(currentState.message) }

        is MatchState.Playing -> {
            MatchView(
                modifier = Modifier.testTag(MATCH_PLAYING_TAG),
                state = currentState,
                onRoll = { viewModel.rollDice() },
                onAccept = { viewModel.acceptPlay() },
                onToggleDice = { index -> viewModel.toggleDiceSelection(index) },
            )
        }

        is MatchState.RoundEnded -> {
            Box(Modifier.fillMaxSize().testTag(STATE_MATCH_ROUND_ENDED)) {
                Box(Modifier.fillMaxSize().background(Color.Black))

                RoundResultOverlay(
                    players = currentState.players,
                    countdown = currentState.countdown,
                )
            }
        }

        is MatchState.MatchEnded -> {
            MatchFinishedView(
                testTag = MATCH_FINISHED_VIEW,
                winner = currentState.winner,
                countdown = currentState.countdown,
            )

            if (currentState.countdown <= 0) {
                LaunchedEffect(Unit) {
                    onNavigateBack(MatchNavigationIntent.BackToLobbies)
                }
            }
        }
    }
}
