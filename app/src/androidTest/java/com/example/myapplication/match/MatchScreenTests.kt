package com.example.myapplication.match

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.myapplication.match.model.output.PlayerOutputModel
import org.junit.Rule
import org.junit.Test

class MatchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel(): MatchViewModel =
        MatchViewModel(
            matchService = FakeMatchService(),
            authRepo = FakeAuthInfoRepo(),
            matchSseService = FakeMatchSseService()
        )


    @Test
    fun loading_state_is_displayed() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            MatchScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag(LOADING_STATE_TAG)
            .assertExists()
            .assertIsDisplayed()
    }


    @Test
    fun error_state_is_displayed() {
        val viewModel = createViewModel()

        // force error
        viewModel.run {
            val field = this::class.java.getDeclaredField("_state")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val stateFlow = field.get(this) as kotlinx.coroutines.flow.MutableStateFlow<MatchState>
            stateFlow.value = MatchState.Error("Error loading match")
        }

        composeTestRule.setContent {
            MatchScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag(ERROR_STATE_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun playing_state_is_displayed() {
        val viewModel = createViewModel()

        // Force state synchronously
        viewModel.run {
            val field = this::class.java.getDeclaredField("_state")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val stateFlow = field.get(this)
                    as kotlinx.coroutines.flow.MutableStateFlow<MatchState>

            stateFlow.value =
                MatchState.Playing(
                    lobbyId = 1,
                    matchInfo = null,
                    currentTurn = null,
                    isMyTurn = true
                )
        }

        composeTestRule.setContent {
            MatchScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag(MATCH_MAIN_BOX)
            .assertExists()
            .assertIsDisplayed()

    }

    @Test
    fun round_ended_state_is_displayed() {
        val viewModel = createViewModel()

        viewModel.run {
            val field = this::class.java.getDeclaredField("_state")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val stateFlow = field.get(this) as kotlinx.coroutines.flow.MutableStateFlow<MatchState>
            stateFlow.value =
                MatchState.RoundEnded(
                    players = emptyList(),
                    countdown = 3
                )
        }

        composeTestRule.setContent {
            MatchScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag(STATE_MATCH_ROUND_ENDED)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun match_ended_state_is_displayed() {
        val viewModel = createViewModel()

        viewModel.run {
            val field = this::class.java.getDeclaredField("_state")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val stateFlow = field.get(this) as kotlinx.coroutines.flow.MutableStateFlow<MatchState>
            stateFlow.value =
                MatchState.MatchEnded(
                    winner =
                        PlayerOutputModel(
                            id = 1,
                            name = "Winner",
                            email = "",
                            balance = 100,
                            gamesPlayed = 0,
                            gamesWon = 0,
                            playerHand = null
                        ),
                    finalRanking = emptyList(),
                    countdown = 5
                )
        }

        composeTestRule.setContent {
            MatchScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag(MATCH_FINISHED_VIEW)
            .assertExists()
            .assertIsDisplayed()
    }
}
