package com.example.myapplication.lobbies

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import org.junit.Rule
import org.junit.Test

class CreateLobbyViewTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private fun createViewModel(): LobbiesViewModel {
        return LobbiesViewModel(
            lobbyService =
                object : FakeLobbyService() {},
            authRepo =
                FakeAuthInfoRepo(
                    storedAuth =
                        AuthInfo(
                            userId = 1,
                            userEmail = "test@email.com",
                            authToken = "fake-token",
                        )
                ),
            matchService = FakeMatchService(),
            lobbySseService = FakeLobbySseService(),
        )
    }

    @Test
    fun all_elements_rendered() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            CreateLobbyView(
                lobbyData =
                    CreateLobbyInputModel(
                        name = "John Test's lobby",
                        description = "test Lobby",
                        minPlayers = 2,
                        maxPlayers = 6,
                        numberOfRounds = 2,
                        costToPlay = 0,
                    ),
                viewModel = viewModel,
            )
        }

        // Scaffold
        composeTestRule.onNodeWithTag(CREATE_LOBBY_SCAFFOLD_TAG)
            .assertExists()
            .assertIsDisplayed()

        // TextFields
        composeTestRule.onNodeWithTag(LOBBY_NAME_TEXTFIELD_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(LOBBY_DESCRIPTION_TEXTFIELD_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(PRICE_TEXTFIELD_TAG)
            .assertExists()
            .assertIsDisplayed()

        // Counters
        composeTestRule.onNodeWithTag(MIN_PLAYERS_COUNTER_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(MAX_PLAYERS_COUNTER_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(NUMBER_OF_ROUNDS_COUNTER_TAG)
            .assertExists()
            .assertIsDisplayed()

        // Button exists but disabled (invalid input)
        composeTestRule.onNodeWithTag(GENERATE_LOBBY_BUTTON_TAG)
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun create_lobby_button_enabled_when_form_is_valid() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            CreateLobbyView(
                lobbyData =
                    CreateLobbyInputModel(
                        name = "My Lobby",
                        description = "Test description",
                        minPlayers = 2,
                        maxPlayers = 6,
                        numberOfRounds = 4,
                        costToPlay = 10,
                    ),
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithTag(GENERATE_LOBBY_BUTTON_TAG)
            .assertExists()
            .assertIsEnabled()
    }

    @Test
    fun create_lobby_button_disabled_when_form_is_valid() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            CreateLobbyView(
                lobbyData =
                    CreateLobbyInputModel(
                        name = "My Lobby",
                        description = "Test description",
                        minPlayers = 2,
                        maxPlayers = 6,
                        numberOfRounds = 4,
                        costToPlay = 0,
                    ),
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithTag(GENERATE_LOBBY_BUTTON_TAG)
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun clicking_create_lobby_button_calls_create_lobby() {
        val viewModel =
            FakeMatchService.SpyLobbiesViewModel(
                lobbyService = object : FakeLobbyService() {},
                authRepo =
                    FakeAuthInfoRepo(
                        storedAuth =
                            AuthInfo(
                                userId = 1,
                                userEmail = "test@email.com",
                                authToken = "fake-token",
                            )
                    ),
                matchService = FakeMatchService(),
                lobbySseService = FakeLobbySseService(),
            )

        val input =
            CreateLobbyInputModel(
                name = "My Lobby",
                description = "Test description",
                minPlayers = 2,
                maxPlayers = 6,
                numberOfRounds = 4,
                costToPlay = 10,
            )

        composeTestRule.setContent {
            CreateLobbyView(
                lobbyData = input,
                viewModel = viewModel,
            )
        }

        composeTestRule
            .onNodeWithTag(GENERATE_LOBBY_BUTTON_TAG)
            .assertIsEnabled()
            .performClick()

        assert(viewModel.createLobbyCalled)
        assert(viewModel.receivedInput == input)
    }

}
