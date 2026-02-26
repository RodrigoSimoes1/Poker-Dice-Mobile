package com.example.myapplication.lobbies

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel
import com.example.myapplication.login.domain.AuthInfo
import org.junit.Rule
import org.junit.Test


class LobbiesSelectionScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    private fun createViewModel(
        initialLobbies: MutableList<com.example.myapplication.lobbies.model.output.LobbyOutputModel> =
            mutableListOf()
    ): LobbiesViewModel {

        val authRepo =
            FakeAuthInfoRepo(
                AuthInfo(
                    userId = 0,
                    userEmail = "test@email.com",
                    authToken = "token"
                )
            )

        val lobbyService =
            object : FakeLobbyService(
                lobbies = initialLobbies
            ) {}

        val matchService = FakeMatchService()
        val sseService = FakeLobbySseService()

        return LobbiesViewModel(
            lobbyService = lobbyService,
            authRepo = authRepo,
            matchService = matchService,
            lobbySseService = sseService
        )
    }


    @Test
    fun test_lobbies_selection_state_is_displayed() {
        val lobby =
            com.example.myapplication.lobbies.model.output.LobbyOutputModel(
                id = 1,
                name = "Test Lobby",
                description = "Description",
                players = emptyList(),
                minPlayers = 2,
                maxPlayers = 6,
                costToPlay = 0,
                numberOfRounds = 3,
                isPublic = true,
                host =
                    com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel(
                        id = 0,
                        name = "Host",
                        email = "test@email.com"
                    )
            )

        val viewModel =
            createViewModel(
                initialLobbies = mutableListOf(lobby)
            )

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(LOBBIES_VIEW_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(CREATE_LOBBY_TAG)
            .assertDoesNotExist()
    }

    @Test
    fun test_creating_lobby_state_is_displayed() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule.runOnUiThread {
            viewModel.startCreatingLobby()
        }

        composeTestRule.onNodeWithTag(CREATE_LOBBY_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun test_waiting_room_state_is_displayed() {
        val lobby =
            com.example.myapplication.lobbies.model.output.LobbyOutputModel(
                id = 1,
                name = "Waiting Lobby",
                description = "Description",
                players = emptyList(),
                minPlayers = 2,
                maxPlayers = 6,
                costToPlay = 0,
                numberOfRounds = 3,
                isPublic = true,
                host =
                    com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel(
                        id = 0,
                        name = "Host",
                        email = "test@email.com"
                    )
            )

        val viewModel = createViewModel()

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule.runOnUiThread {
            viewModel.returnToLobby(lobby)
        }

        composeTestRule.onNodeWithTag(WAITING_ROOM_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

/*    @Test
    fun test_error_state_is_displayed() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule.runOnUiThread {
            viewModel.loadLobbies("Erro")
        }

        composeTestRule.onNodeWithTag(RETRY_BUTTON_TAG)
            .assertExists()
            .assertIsDisplayed()
    }*/

    @Test
    fun test_navigate_to_match_state_shows_loading() {
        val viewModel = createViewModel()

        val lobby =
            com.example.myapplication.lobbies.model.output.LobbyOutputModel(
                id = 1,
                name = "Lobby",
                description = "",
                players = emptyList(),
                minPlayers = 2,
                maxPlayers = 6,
                costToPlay = 0,
                numberOfRounds = 3,
                isPublic = true,
                host =
                    com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel(
                        id = 0,
                        name = "Host",
                        email = "test@email.com"
                    )
            )

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule.runOnUiThread {
            viewModel.returnToLobby(lobby)
            viewModel.startMatch(lobby.id) {}
        }

        composeTestRule.onNodeWithTag(ENTERING_LOBBY_INDICATOR_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun clicking_create_lobby_fab_calls_start_creating_lobby() {
        val viewModel =
            FakeMatchService.SpyLobbiesViewModelSelection(
                LobbiesScreenState.LobbiesSelection(
                    lobbies = emptyList(),
                    currentUserId = 1,
                )
            )

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithTag(CREATE_LOBBY_BUTTON_TAG)
            .assertExists()
            .performClick()

        assert(viewModel.startCreatingLobbyCalled)
    }

    @Test
    fun clicking_retry_button_calls_load_lobbies() {
        val viewModel =
            FakeMatchService.SpyLobbiesViewModelSelection(
                LobbiesScreenState.Error("Network error")
            )

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithTag(RETRY_BUTTON_TAG)
            .assertExists()
            .performClick()

        assert(viewModel.loadLobbiesCalled)
    }

    @Test
    fun confirm_leave_lobby_calls_leave_lobby() {

        val viewModel =
            FakeMatchService.SpyLobbiesViewModelSelection(
                LobbiesScreenState.WaitingRoom(
                    lobby = LobbyOutputModel(id = 1, name = "test lobby", description = "test",
                                        host = UserInLobbyOutputModel(id = 1, name = "test", email = "test@email.com"),
                                        players = listOf(UserInLobbyOutputModel(id = 1, name = "test", email = "test@email.com")),
                                        minPlayers = 2,
                                        maxPlayers = 6,
                                        costToPlay = 5,
                                        numberOfRounds = 2,
                                        isPublic = true,
                                        ),
                    currentUserId = "1",
                )
            )

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()


        composeTestRule
            .onNodeWithTag(LEAVING_LOBBY_BUTTON_TAG)
            .assertExists()
            .performClick()

        assert(viewModel.leaveLobbyCalledWith == 1)
    }

    @Test
    fun cancel_leave_lobby_does_not_call_leave_lobby() {
        val viewModel =
            FakeMatchService.SpyLobbiesViewModelSelection(
                LobbiesScreenState.WaitingRoom(
                    lobby = LobbyOutputModel(id = 1, name = "test lobby", description = "test",
                        host = UserInLobbyOutputModel(id = 1, name = "test", email = "test@email.com"),
                        players = listOf(UserInLobbyOutputModel(id = 1, name = "test", email = "test@email.com")),
                        minPlayers = 2,
                        maxPlayers = 6,
                        costToPlay = 5,
                        numberOfRounds = 2,
                        isPublic = true,
                    ),
                    currentUserId = "1",
                )
            )

        composeTestRule.setContent {
            LobbiesSelectionScreen(viewModel = viewModel)
        }

        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()

        composeTestRule
            .onNodeWithTag(CANCEL_CLOSE_LOBBY_BUTTON_TAG)
            .performClick()

        assert(viewModel.leaveLobbyCalledWith == null)
    }




}

