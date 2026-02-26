package com.example.myapplication.shop

import com.example.myapplication.shop.model.BalancePackage
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShopVMTests {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ShopViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val authRepo = FakeAuthInfoRepo()
        val userService = FakeUserService()
        val balanceService = FakeBalanceService(userService)

        viewModel = ShopViewModel(
            service = balanceService,
            authRepo = authRepo,
            userService = userService
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_load_with_balance() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as ShopScreenState.Success
        assertEquals(100, state.currentBalance)
        assertFalse(state.isPurchasing)
    }

    @Test
    fun loading_balance_goes_into_success_state() = runTest {
        viewModel.loadUserBalance()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is ShopScreenState.Success)
    }

    @Test
    fun purchase_updates_balance() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.purchaseBalance(
            BalancePackage("SMALL_TEST", coin = 10, realMoney = 10)
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as ShopScreenState.Success
        assertEquals(150, state.currentBalance)
        assertFalse(state.isPurchasing)
    }
}

