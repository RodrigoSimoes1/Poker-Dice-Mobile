package com.example.myapplication.profile

import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.profile.model.UserOutputModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val fakeUser = UserOutputModel(
        id = 1,
        name = "John Test",
        email = "test@email.com",
        balance = 100,
        gamesPlayed = 10,
        gamesWon = 5,
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_by_email_and_verify_success() = runTest {
        val fakeUserService = FakeUserService(users = listOf(fakeUser))
        val fakeAuthRepo = FakeAuthInfoRepo(AuthInfo("test@email.com", "token", 1))
        val viewModel = ProfileViewModel(fakeUserService, fakeAuthRepo, FakeLogoutUseCase())

        assert(viewModel.state.value is ProfileScreenState.Loading)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is ProfileScreenState.Success)
        assertEquals(fakeUser.email, (state as ProfileScreenState.Success).user.email)
    }

    @Test
    fun load_fails_when_no_auth_info() = runTest {
        val fakeUserService = FakeUserService(users = listOf(fakeUser))
        val fakeAuthRepo = FakeAuthInfoRepo(initialAuthInfo = null)

        val viewModel = ProfileViewModel(fakeUserService, fakeAuthRepo, FakeLogoutUseCase())

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is ProfileScreenState.Error)
        assertEquals("Nenhuma sessão ativa.", (state as ProfileScreenState.Error).message)
    }

    @Test
    fun load_fails_when_user_not_found() = runTest {
        val fakeUserService = FakeUserService(users = listOf(fakeUser))
        val fakeAuthRepo = FakeAuthInfoRepo(AuthInfo("unknown@email.com", "token", 2))

        val viewModel = ProfileViewModel(fakeUserService, fakeAuthRepo, FakeLogoutUseCase())

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is ProfileScreenState.Error)
        assertEquals("Utilizador não encontrado.", (state as ProfileScreenState.Error).message)
    }

    @Test
    fun load_fails_when_service_returns_failure() = runTest {
        val failingService = FakeUserService(shouldFail = true)
        val fakeAuthRepo = FakeAuthInfoRepo(AuthInfo("test@email.com", "token", 1))

        val viewModel = ProfileViewModel(failingService, fakeAuthRepo, FakeLogoutUseCase())

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is ProfileScreenState.Error)
        assertEquals("Erro ao carregar utilizadores.", (state as ProfileScreenState.Error).message)
    }

    @Test
    fun load_fails_on_exception() = runTest {
        val throwingService = FakeUserService(shouldThrow = true)
        val fakeAuthRepo = FakeAuthInfoRepo(AuthInfo("test@email.com", "token", 1))

        val viewModel = ProfileViewModel(throwingService, fakeAuthRepo, FakeLogoutUseCase())

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is ProfileScreenState.Error)
        assertEquals("Fake exception thrown", (state as ProfileScreenState.Error).message)
    }

    @Test
    fun double_load_ignored_when_loading() = runTest {
        val fakeUserService = FakeUserService(users = listOf(fakeUser))
        val fakeAuthRepo = FakeAuthInfoRepo(AuthInfo("test@email.com", "token", 1))

        val viewModel = ProfileViewModel(fakeUserService, fakeAuthRepo, FakeLogoutUseCase())

        viewModel.loadUserByEmail()
        viewModel.loadUserByEmail()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeUserService.callCount)
    }

    @Test
    fun generate_invite_updates_success_state() = runTest {
        val fakeUserService = FakeUserService(users = listOf(fakeUser))
        val fakeAuthRepo = FakeAuthInfoRepo(AuthInfo("test@email.com", "token", 1))

        val viewModel = ProfileViewModel(fakeUserService, fakeAuthRepo, FakeLogoutUseCase())
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.generateInvite()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as ProfileScreenState.Success
        assertNotNull(state.inviteCode)
    }

    @Test
    fun generate_invite_ignored_when_not_success() = runTest {
        val fakeUserService = FakeUserService(users = listOf(fakeUser))
        val fakeAuthRepo = FakeAuthInfoRepo(initialAuthInfo = null)

        val viewModel = ProfileViewModel(fakeUserService, fakeAuthRepo, FakeLogoutUseCase())

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is ProfileScreenState.Error)

        val before = viewModel.state.value
        viewModel.generateInvite()

        assertEquals(before, viewModel.state.value)
    }
}
