package com.example.myapplication.shop

import SHOP_CIRCULAR_PROGRESS_INDICATOR_TAG
import SHOP_ERROR_BOX_TAG
import SHOP_SCREEN_VIEW_TAG
import ShopScreen
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class ShopScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shop_error_state_is_displayed_when_no_session_exists() {
        val viewModel =
            ShopViewModel(
                service = FakeBalanceService(FakeUserService()),
                authRepo = FakeAuthInfoRepo(initialAuthInfo = null),
                userService = FakeUserService()
            )

        composeTestRule.setContent {
            ShopScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag(SHOP_ERROR_BOX_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_CIRCULAR_PROGRESS_INDICATOR_TAG)
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag(SHOP_SCREEN_VIEW_TAG)
            .assertDoesNotExist()
    }


    @Test
    fun shop_success_state_is_displayed_when_user_balance_is_loaded() {
        val userService = FakeUserService()

        val viewModel =
            ShopViewModel(
                service = FakeBalanceService(userService),
                authRepo = FakeAuthInfoRepo(),
                userService = userService
            )

        composeTestRule.setContent {
            ShopScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag(SHOP_SCREEN_VIEW_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_CIRCULAR_PROGRESS_INDICATOR_TAG)
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag(SHOP_ERROR_BOX_TAG)
            .assertDoesNotExist()
    }
}
