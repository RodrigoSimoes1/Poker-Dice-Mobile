package com.example.myapplication.lobbies
import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import com.example.myapplication.lobbies.model.output.LobbyEvent
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.match.domain.MatchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LobbiesViewModelTests {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LobbiesViewModel
    private lateinit var fakeAuthRepo: FakeAuthInfoRepo
    private lateinit var fakeLobbyService: FakeLobbyService
    private lateinit var fakeMatchService: MatchService
    private lateinit var fakeSseService: FakeLobbySseService

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        fakeAuthRepo =
            FakeAuthInfoRepo(
                AuthInfo(
                    authToken = "TOKEN",
                    userId = 1,
                    userEmail = "test@email.com"
                )
            )

        val lobby =
            LobbyOutputModel(
                id = 1,
                name = "Test Lobby",
                description = "Desc",
                players = listOf(UserInLobbyOutputModel(1, "John", "test@email.com")),
                minPlayers = 2,
                maxPlayers = 6,
                costToPlay = 10,
                numberOfRounds = 2,
                isPublic = true,
                host = UserInLobbyOutputModel(1, "John", "test@email.com")
            )

        fakeLobbyService =
            object : FakeLobbyService(
                lobbies = mutableListOf(lobby),
                myLobby = null
            ) {}

        fakeMatchService = FakeMatchService()
        fakeSseService = FakeLobbySseService()

        viewModel =
            LobbiesViewModel(
                lobbyService = fakeLobbyService,
                authRepo = fakeAuthRepo,
                matchService = fakeMatchService,
                lobbySseService = fakeSseService
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    //test to load lobbies
    @Test
    fun load_lobbies_success() = runTest {
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.LobbiesSelection)

        val lobbiesState = state as LobbiesScreenState.LobbiesSelection
        assertEquals(1, lobbiesState.lobbies.size)
        assertEquals(1, lobbiesState.currentUserId)
    }

    //test to create a lobby
    @Test
    fun create_lobby_success() = runTest {
        advanceUntilIdle()

        val input =
            CreateLobbyInputModel(
                name = "New Lobby",
                description = "Desc",
                minPlayers = 2,
                maxPlayers = 4,
                numberOfRounds = 3,
                costToPlay = 5
            )

        viewModel.createLobby(input)
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.WaitingRoom)

        val waitingState = state as LobbiesScreenState.WaitingRoom
        assertEquals("New Lobby", waitingState.lobby.name)
    }

    //test to join a lobby
    @Test
    fun join_lobby_success() = runTest {
        advanceUntilIdle()

        viewModel.joinLobby(1)
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.WaitingRoom)
    }

    //test to leave a lobby
    @Test
    fun leave_lobby_returns_to_lobbies() = runTest {
        advanceUntilIdle()

        viewModel.joinLobby(1)
        advanceUntilIdle()

        viewModel.leaveLobby(1)
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.LobbiesSelection)
    }

    //test to start a match
    @Test
    fun start_match_navigates() = runTest {
        advanceUntilIdle()

        viewModel.joinLobby(1)
        advanceUntilIdle()

        var navigated = false

        viewModel.startMatch(
            lobbyId = 1,
            onNavigateToMatch = { navigated = true }
        )

        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.NavigateToMatch)
        assertTrue(navigated)
    }

    @Test
    fun global_sse_adds_lobby_to_list() = runTest {
        advanceUntilIdle()

        val newLobby = LobbyOutputModel(
            id = 2,
            name = "New",
            description = "",
            players = listOf(),
            minPlayers = 2,
            maxPlayers = 6,
            costToPlay = 0,
            numberOfRounds = 2,
            isPublic = true,
            host = UserInLobbyOutputModel(1, "John", "test@email.com")
        )

        //emit a global event
        fakeSseService.emitGlobal(LobbyEvent.AddOrUpdate(newLobby))
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.LobbiesSelection)
        val lobbies = (state as LobbiesScreenState.LobbiesSelection).lobbies
        assertEquals(2, lobbies.size)
        assertTrue(lobbies.any { it.id == 2 })
    }

    @Test
    fun global_sse_removes_lobby_from_list() = runTest {
        advanceUntilIdle()

        //emit a removal event
        fakeSseService.emitGlobal(LobbyEvent.Remove(1))
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.LobbiesSelection)
        val lobbies = (state as LobbiesScreenState.LobbiesSelection).lobbies
        assertEquals(0, lobbies.size)
    }

    @Test
    fun lobby_sse_player_joined_adds_new_player() = runTest {
        //enter the waiting room
        advanceUntilIdle()
        viewModel.joinLobby(1)
        advanceUntilIdle()

        val event = LobbyEvent.PlayerJoined(
            userId = 5,
            name = "Alice",
            email = "alice@test.com"
        )

        fakeSseService.emitLobby(event)
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.WaitingRoom)

        val players = (state as LobbiesScreenState.WaitingRoom).lobby.players
        assertTrue(players.any { it.id == 5 })
    }

    @Test
    fun lobby_sse_player_left_removes_player() = runTest {
        advanceUntilIdle()
        viewModel.joinLobby(1)
        advanceUntilIdle()

        //remove the existing player
        fakeSseService.emitLobby(LobbyEvent.PlayerLeft(userId = 1))
        advanceUntilIdle()

        val state = viewModel.currentState.value
        assertTrue(state is LobbiesScreenState.WaitingRoom)

        val players = (state as LobbiesScreenState.WaitingRoom).lobby.players
        assertEquals(0, players.size)
    }

    //tests for creation logic
    @Test
    fun update_lobby_name_changes_state() = runTest {
        advanceUntilIdle()
        viewModel.startCreatingLobby()

        viewModel.updateLobbyName("NewName")
        val state = viewModel.currentState.value

        assertTrue(state is LobbiesScreenState.CreatingLobby)
        assertEquals("NewName", (state as LobbiesScreenState.CreatingLobby).lobby.name)
    }

    @Test
    fun inc_min_players_never_exceeds_max_players() = runTest {
        advanceUntilIdle()
        viewModel.startCreatingLobby()

        repeat(10) { viewModel.incMinPlayers() }
        val state = viewModel.currentState.value

        assertTrue(state is LobbiesScreenState.CreatingLobby)
        val lobby = (state as LobbiesScreenState.CreatingLobby).lobby

        assertTrue(lobby.minPlayers <= lobby.maxPlayers)
    }

}
