package com.example.myapplication.lobbies

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel
import org.junit.Rule
import org.junit.Test

class WaitingRoomViewTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val hostUser =
        UserInLobbyOutputModel(
            id = 1,
            name = "Test Host",
            email = "testHost@email.com",
        )

    private val otherUser =
        UserInLobbyOutputModel(
            id = 2,
            name = "other test user",
            email = "other@email.com",
        )

    private fun fakeLobby(): LobbyOutputModel =
        LobbyOutputModel(
            id = 1,
            name = "Test Lobby",
            description = "Test Description",
            isPublic = true,
            host = hostUser,
            players = listOf(hostUser, otherUser),
            minPlayers = 2,
            maxPlayers = 6,
            numberOfRounds = 3,
            costToPlay = 10,
        )

    @Test
    fun waiting_room_renders_common_elements() {
        composeTestRule.setContent {
            WaitingRoomView(
                lobby = fakeLobby(),
                currentUserId = hostUser.email,
                onLeave = {},
                onStart = {},
            )
        }

        composeTestRule.onNodeWithTag(WAITING_ROOM_SCAFFOLD_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(LOBBY_CARD_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(PLAYERS_COLUMN_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(LEAVE_LOBBY_BUTTON_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun host_sees_start_game_button() {
        composeTestRule.setContent {
            WaitingRoomView(
                lobby = fakeLobby(),
                currentUserId = hostUser.email,
                onLeave = {},
                onStart = {},
            )
        }

        composeTestRule.onNodeWithTag(START_GAME_BUTTON_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(WAITING_TAG)
            .assertDoesNotExist()
    }

    @Test
    fun non_host_sees_waiting_message() {
        composeTestRule.setContent {
            WaitingRoomView(
                lobby = fakeLobby(),
                currentUserId = otherUser.email,
                onLeave = {},
                onStart = {},
            )
        }

        composeTestRule.onNodeWithTag(WAITING_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(START_GAME_BUTTON_TAG)
            .assertDoesNotExist()
    }

    @Test
    fun clicking_start_game_button_calls_onStart() {
        var startCalled = false

        composeTestRule.setContent {
            WaitingRoomView(
                lobby = fakeLobby(),
                currentUserId = hostUser.email,
                onLeave = {},
                onStart = { startCalled = true },
            )
        }

        composeTestRule
            .onNodeWithTag(START_GAME_BUTTON_TAG)
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        assert(startCalled)
    }

    @Test
    fun clicking_leave_lobby_button_calls_onLeave() {
        var leaveCalled = false

        composeTestRule.setContent {
            WaitingRoomView(
                lobby = fakeLobby(),
                currentUserId = hostUser.email,
                onLeave = { leaveCalled = true },
                onStart = {},
            )
        }

        composeTestRule
            .onNodeWithTag(LEAVE_LOBBY_BUTTON_TAG)
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        assert(leaveCalled)
    }

    @Test
    fun non_host_cannot_start_game() {
        var startCalled = false

        composeTestRule.setContent {
            WaitingRoomView(
                lobby = fakeLobby(),
                currentUserId = otherUser.email,
                onLeave = {},
                onStart = { startCalled = true },
            )
        }

        composeTestRule
            .onNodeWithTag(WAITING_TAG)
            .assertExists()
            .assertIsDisplayed()
            .assertIsNotEnabled()

        assert(!startCalled)
    }


}
