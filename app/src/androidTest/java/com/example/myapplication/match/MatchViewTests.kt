package com.example.myapplication.match

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.myapplication.domainlogic.Dice
import com.example.myapplication.domainlogic.DiceFace
import com.example.myapplication.domainlogic.Hand
import com.example.myapplication.domainlogic.HandRank
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.MatchStatusOutputModel
import com.example.myapplication.match.model.output.PlayerOutputModel
import org.junit.Rule
import org.junit.Test

class MatchViewTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun playingState(
        isMyTurn: Boolean = true,
        triesLeft: Int = 2,
        isRolling: Boolean = false,
    ): MatchState.Playing =
        MatchState.Playing(
            lobbyId = 1,
            matchInfo = MatchStatusOutputModel(
                id = 1,
                lobbyId = 1,
                isFinished = false,
                players = listOf(
                    PlayerOutputModel(
                        id = 1,
                        name = "John Test",
                        email = "test@email.com",
                        balance = 100,
                        gamesPlayed = 0,
                        gamesWon = 0,
                        playerHand = null,
                    ),
                ),
            ),
            currentTurn = CurrentTurnOutputModel(
                userId = 1,
                userName = "John Test",
                pointsEarned = 0,
                dices = List(5) { Dice(it + 1, DiceFace.ACE) },
                handRank = HandRank.ONE_PAIR,
                triesLeft = triesLeft,
            ),
            isMyTurn = isMyTurn,
            dicesToKeep = listOf(false, false, false, false, false),
            isRolling = isRolling,
        )


    @Test
    fun test_when_in_play(){
        composeTestRule.setContent {
            MatchView(
                state = MatchState.Playing(
                    lobbyId = 1,
                    matchInfo = MatchStatusOutputModel(id = 1, lobbyId = 1, players = listOf(
                        PlayerOutputModel(1, "John Test", "test@email.com", 100,
                            Hand(listOf(Dice(0, DiceFace.ACE), Dice(1, DiceFace.ACE), Dice(2, DiceFace.NINE), Dice(3, DiceFace.KING), Dice(4, DiceFace.TEN)),
                                HandRank.ONE_PAIR), 3, 2)
                    ), isFinished = false),
                    currentTurn = CurrentTurnOutputModel(1, "John Test", 10,
                                listOf(Dice(0, DiceFace.ACE), Dice(1, DiceFace.ACE), Dice(2, DiceFace.NINE), Dice(3, DiceFace.KING), Dice(4, DiceFace.TEN))
                                    , HandRank.ONE_PAIR, triesLeft = 1),
                    isMyTurn = true,
                    dicesToKeep = listOf(false, false, false, false, false),
                    isRolling = true,
                ),
                onRoll = {},
                onAccept = {},
                onToggleDice = {},
            )
        }

        composeTestRule.onNodeWithTag(MATCH_MAIN_BOX).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(MATCH_TABLE_BASE).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(MATCH_DICE_AREA_PLAYING).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(MATCH_DICE_AREA_OUT_OF_PLAY).assertDoesNotExist()
        composeTestRule.onNodeWithTag(MATCH_PLAYERS_TAG).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(REROLL_HAND_TAG).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(ACCEPT_HAND_BUTTON_TAG).assertExists().assertIsDisplayed()

    }

    @Test
    fun test_when_out_of_play(){
        composeTestRule.setContent {
            MatchView(
                state = MatchState.Playing(
                    lobbyId = 1,
                    matchInfo = MatchStatusOutputModel(id = 1, lobbyId = 1, players = listOf(
                        PlayerOutputModel(1, "John Test", "test@email.com", 100,
                            Hand(listOf(Dice(0, DiceFace.ACE), Dice(1, DiceFace.ACE), Dice(2, DiceFace.NINE), Dice(3, DiceFace.KING), Dice(4, DiceFace.TEN)),
                                HandRank.ONE_PAIR), 3, 2)
                    ), isFinished = false),
                    currentTurn = CurrentTurnOutputModel(1, "John Test", 10,
                        listOf(Dice(0, DiceFace.ACE), Dice(1, DiceFace.ACE), Dice(2, DiceFace.NINE), Dice(3, DiceFace.KING), Dice(4, DiceFace.TEN))
                        , HandRank.ONE_PAIR, triesLeft = 1),
                    isMyTurn = false,
                    dicesToKeep = listOf(false, false, false, false, false),
                    isRolling = false,
                ),
                onRoll = {},
                onAccept = {},
                onToggleDice = {},
            )
        }

        composeTestRule.onNodeWithTag(MATCH_MAIN_BOX).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(MATCH_TABLE_BASE).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(MATCH_DICE_AREA_PLAYING).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(MATCH_DICE_AREA_OUT_OF_PLAY).assertDoesNotExist()
        composeTestRule.onNodeWithTag(MATCH_PLAYERS_TAG).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(REROLL_HAND_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(ACCEPT_HAND_BUTTON_TAG).assertDoesNotExist()

    }

    @Test
    fun clicking_roll_button_calls_onRoll() {
        var rollCalled = false

        composeTestRule.setContent {
            MatchView(
                state = playingState(triesLeft = 2),
                onRoll = { rollCalled = true },
                onAccept = {},
                onToggleDice = {},
            )
        }

        composeTestRule
            .onNodeWithTag(REROLL_HAND_TAG)
            .assertIsDisplayed()
            .performClick()

        assert(rollCalled)
    }

    @Test
    fun clicking_accept_button_calls_onAccept() {
        var acceptCalled = false

        composeTestRule.setContent {
            MatchView(
                state = playingState(triesLeft = 1),
                onRoll = {},
                onAccept = { acceptCalled = true },
                onToggleDice = {},
            )
        }

        composeTestRule
            .onNodeWithTag(ACCEPT_HAND_BUTTON_TAG)
            .assertIsDisplayed()
            .performClick()

        assert(acceptCalled)
    }

    @Test
    fun clicking_dice_checkbox_calls_onToggleDice_with_index() {
        val toggledIndexes = mutableListOf<Int>()

        composeTestRule.setContent {
            MatchView(
                state = playingState(triesLeft = 2),
                onRoll = {},
                onAccept = {},
                onToggleDice = { index -> toggledIndexes.add(index) },
            )
        }

        // Click the first checkbox (dice index 0)
        composeTestRule
            .onAllNodes(isToggleable())
            .onFirst()
            .performClick()

        assert(toggledIndexes.contains(0))
    }

    @Test
    fun roll_button_disabled_while_rolling_does_not_call_onRoll() {
        var rollCalled = false

        composeTestRule.setContent {
            MatchView(
                state = playingState(isRolling = true),
                onRoll = { rollCalled = true },
                onAccept = {},
                onToggleDice = {},
            )
        }

        composeTestRule
            .onNodeWithTag(REROLL_HAND_TAG)
            .assertExists()
            .assertIsNotEnabled()

        assert(!rollCalled)
    }

    @Test
    fun when_not_my_turn_buttons_are_not_clickable_or_visible() {
        var rollCalled = false
        var acceptCalled = false

        composeTestRule.setContent {
            MatchView(
                state = playingState(isMyTurn = false),
                onRoll = { rollCalled = true },
                onAccept = { acceptCalled = true },
                onToggleDice = {},
            )
        }

        composeTestRule.onNodeWithTag(REROLL_HAND_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(ACCEPT_HAND_BUTTON_TAG).assertDoesNotExist()

        assert(!rollCalled)
        assert(!acceptCalled)
    }



}