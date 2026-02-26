package com.example.myapplication.login

import com.example.myapplication.login.domain.RegisterData
import com.example.myapplication.login.domain.UserCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_is_idle() = runTest {
        val vm = AuthViewModel(
            loginUseCase = FakeLoginUseCase(true),
            registerUseCase = FakeRegisterUseCase(true),
            service = FakeAuthService(),
            authRepo = FakeAuthRepo(),
        )

        assertTrue(vm.currentState.value is AuthScreenState.Idle)
    }

    @Test
    fun login_success_emits_LoginSuccess() = runTest {
        val vm = AuthViewModel(
            FakeLoginUseCase(true),
            FakeRegisterUseCase(true),
            FakeAuthService(),
            FakeAuthRepo()
        )

        vm.login(UserCredentials("test@email.com", "123"))
        advanceUntilIdle()

        val state = vm.currentState.value
        assertTrue(state is AuthScreenState.LoginSuccess)
        assertEquals("TEST_TOKEN", (state as AuthScreenState.LoginSuccess).authToken)
    }

    @Test
    fun register_success_emits_RegisterSuccess() = runTest {
        val vm = AuthViewModel(
            FakeLoginUseCase(true),
            FakeRegisterUseCase(true),
            FakeAuthService(),
            FakeAuthRepo()
        )

        vm.register(RegisterData("John Test", "email@test.com", "password", "invite"))
        advanceUntilIdle()

        assertTrue(vm.currentState.value is AuthScreenState.RegisterSuccess)
    }

    @Test
    fun register_failure_emits_AuthError() = runTest {
        val vm = AuthViewModel(
            FakeLoginUseCase(true),
            FakeRegisterUseCase(false),
            FakeAuthService(),
            FakeAuthRepo()
        )

        vm.register(RegisterData("John Test", "email@test.com", "password", "invite"))
        advanceUntilIdle()

        assertTrue(vm.currentState.value is AuthScreenState.AuthError)
    }
}