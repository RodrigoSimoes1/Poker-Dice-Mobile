package com.example.myapplication.lobbies

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel
import org.junit.Rule
import org.junit.Test
import com.example.myapplication.R
import androidx.compose.ui.res.stringResource

class LobbiesViewTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val currentUserId = 1

    /**
     * Helper to create a real ViewModel with fake dependencies
     */
    private fun createViewModel(
        lobbies: List<LobbyOutputModel> = emptyList()
    ): LobbiesViewModel {
        val authRepo =
            FakeAuthInfoRepo(
                storedAuth =
                    AuthInfo(
                        userId = currentUserId,
                        userEmail = "test@email.com",
                        authToken = "fake-token",
                    )
            )

        val lobbyService =
            object : FakeLobbyService(
                lobbies = lobbies.toMutableList()
            ) {}

        val lobbySseService = FakeLobbySseService()
        val matchService = FakeMatchService()
        return LobbiesViewModel(
            authRepo = authRepo,
            lobbyService = lobbyService,
            lobbySseService = lobbySseService,
            matchService = matchService
        )
    }

    @Test
    fun when_no_visible_lobbies_show_empty_state() {
        val viewModel = createViewModel(lobbies = emptyList())

        composeTestRule.setContent {
            LobbiesView(
                viewModel = viewModel,
                lobbies = emptyList(),
                currentUserId = currentUserId,
                onCreateClick = {},
            )
        }

        composeTestRule.onNodeWithTag(LOBBIES_SCAFFOLD_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(REFRESH_BUTTON_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(NO_LOBBIES_BOX_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(NO_LOBBIES_TEXT_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(LOBBIES_AVAILABLE_TAG)
            .assertDoesNotExist()
    }

    @Test
    fun when_visible_lobbies_exist_show_lobby_list() {
        val lobbies =
            listOf(
                LobbyOutputModel(
                    id = 1,
                    name = "Lobby Test",
                    description = "Test lobby",
                    isPublic = true,
                    host = UserInLobbyOutputModel(1, "John Test", "test@email.com"),
                    players =
                        listOf(
                            UserInLobbyOutputModel(1, "John Test", "test@email.com"),
                            UserInLobbyOutputModel(2, "Alice Test", "tests@email.com"),
                        ),
                    minPlayers = 2,
                    maxPlayers = 6,
                    numberOfRounds = 5,
                    costToPlay = 10,
                )
            )

        val viewModel = createViewModel(lobbies)

        composeTestRule.setContent {
            LobbiesView(
                viewModel = viewModel,
                lobbies = lobbies,
                currentUserId = currentUserId,
                onCreateClick = {},
            )
        }

        composeTestRule.onNodeWithTag(LOBBIES_AVAILABLE_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(NO_LOBBIES_BOX_TAG)
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag(NO_LOBBIES_TEXT_TAG)
            .assertDoesNotExist()
    }

    @Test
    fun create_lobby_fab_is_always_visible() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            LobbiesView(
                viewModel = viewModel,
                lobbies = emptyList(),
                currentUserId = currentUserId,
                onCreateClick = {},
            )
        }

        composeTestRule.onNodeWithTag(CREATE_LOBBY_BUTTON_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun clicking_create_lobby_fab_calls_onCreateClick() {
        var createClicked = false
        val viewModel = createViewModel()

        composeTestRule.setContent {
            LobbiesView(
                viewModel = viewModel,
                lobbies = emptyList(),
                currentUserId = currentUserId,
                onCreateClick = { createClicked = true },
            )
        }

        composeTestRule
            .onNodeWithTag(CREATE_LOBBY_BUTTON_TAG)
            .assertExists()
            .performClick()

        assert(createClicked)
    }

    @Test
    fun clicking_refresh_button_calls_load_lobbies() {
        val viewModel =
            FakeMatchService.SpyLobbiesViewModelSelection(
                initialState =
                    LobbiesScreenState.LobbiesSelection(
                        lobbies = emptyList(),
                        currentUserId = currentUserId
                    )
            )

        composeTestRule.setContent {
            LobbiesView(
                viewModel = viewModel,
                lobbies = emptyList(),
                currentUserId = currentUserId,
                onCreateClick = {},
            )
        }

        composeTestRule
            .onNodeWithTag(REFRESH_BUTTON_TAG)
            .assertExists()
            .performClick()

        assert(viewModel.loadLobbiesCalled)
    }

    /*@Test
    fun clicking_join_lobby_calls_join_lobby() {
        val lobby =
            LobbyOutputModel(
                id = 1,
                name = "Test Lobby",
                description = "",
                isPublic = true,
                host = UserInLobbyOutputModel(2, "Host", "host@email.com"),
                players = emptyList(),
                minPlayers = 2,
                maxPlayers = 6,
                numberOfRounds = 3,
                costToPlay = 0,
            )

        val viewModel =
            FakeMatchService.SpyLobbiesViewModelSelection(
                LobbiesScreenState.LobbiesSelection(
                    lobbies = listOf(lobby),
                    currentUserId = currentUserId
                )
            )

        composeTestRule.setContent {
            LobbiesView(
                viewModel = viewModel,
                lobbies = listOf(lobby),
                currentUserId = currentUserId,
                onCreateClick = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.stringResource(R.string.text_to_enter_the_lobby)
            )
            .performClick()

        assert(viewModel.joinLobbyCalledWith == lobby.id)
    }*/

}
