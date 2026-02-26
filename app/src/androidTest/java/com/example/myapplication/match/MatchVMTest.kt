package com.example.myapplication.match

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MatchViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MatchViewModel

    @Before
    fun setup() {
        viewModel =
            MatchViewModel(
                matchService = FakeMatchService(),
                authRepo = FakeAuthInfoRepo(),
                matchSseService = FakeMatchSseService(),
            )
    }

    @Test
    fun loadMatch_emits_playing_state() = runTest {
        viewModel.loadMatch(lobbyId = 10, hostId = 1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is MatchState.Playing)

        state as MatchState.Playing
        assertEquals(10, state.lobbyId)
        assertTrue(state.isMyTurn)
        assertNotNull(state.currentTurn)
    }

    @Test
    fun rollDice_updates_current_turn() = runTest {
        viewModel.loadMatch(lobbyId = 1, hostId = 1)
        advanceUntilIdle()

        viewModel.rollDice()
        advanceUntilIdle()

        val state = viewModel.state.value as MatchState.Playing
        assertEquals(2, state.currentTurn!!.triesLeft)
        assertEquals(5, state.currentTurn.dices.size)
    }

    @Test
    fun acceptPlay_ends_turn() = runTest {
        viewModel.loadMatch(lobbyId = 1, hostId = 1)
        advanceUntilIdle()

        viewModel.acceptPlay()
        advanceUntilIdle()

        val state = viewModel.state.value as MatchState.Playing
        assertFalse(state.isMyTurn)
        assertFalse(state.isRolling)
    }

    @Test
    fun rollDice_does_nothing_if_not_my_turn() = runTest {
        viewModel.loadMatch(lobbyId = 1, hostId = 2)
        advanceUntilIdle()

        val before = viewModel.state.value

        viewModel.rollDice()
        advanceUntilIdle()

        assertEquals(before, viewModel.state.value)
    }
}
