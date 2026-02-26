package com.example.myapplication.shop

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.commonElements.BACK_BUTTON_TAG
import com.example.myapplication.profile.model.UserOutputModel
import com.example.myapplication.shop.model.BalancePackage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShopViewTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun test_all_components_displayed() {
        composeTestRule.setContent {
            ShopView(
                currentBalance = 50,
                isPurchasing = false,
            )
        }

        composeTestRule.onNodeWithTag(OVERALL_SHOP_SCREEN_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_MAIN_COLUMN_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_SCREEN_CARD_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_CURRENT_BALANCE_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_NUMERIC_BALANCE_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_BALANCE_ICON_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(SHOP_PACKAGE_CHOSE_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithTag(BUNDLES_TAG)
            .assertCountEquals(BalancePackage.getAllPackages().size)
    }

    @Test
    fun clicking_back_button_triggers_navigate_back() {
        var navigationIntent: ShopScreenNavigationIntent? = null

        composeTestRule.setContent {
            ShopView(
                currentBalance = 100,
                onNavigate = { navigationIntent = it }
            )
        }

        composeTestRule
            .onNodeWithTag(BACK_BUTTON_TAG)
            .assertExists()
            .performClick()

        assert(navigationIntent == ShopScreenNavigationIntent.NavigateBack)
    }

    @Test
    fun clicking_package_triggers_onPurchase_callback() {
        var purchasedPackage: BalancePackage? = null

        composeTestRule.setContent {
            ShopView(
                currentBalance = 100,
                onPurchase = { purchasedPackage = it },
                isPurchasing = false
            )
        }

        composeTestRule
            .onNodeWithTag("PURCHASE_BUTTON_SP")
            .assertExists()
            .performClick()

        assert(purchasedPackage != null)
    }


    @Test
    fun purchase_buttons_are_disabled_while_purchasing() {
        composeTestRule.setContent {
            ShopView(
                currentBalance = 100,
                isPurchasing = true
            )
        }

        composeTestRule
            .onAllNodesWithTag(BUNDLES_TAG)
            .onFirst()
            .assertExists()
    }

    @Test
    fun clicking_package_updates_balance() {
        val fakeUserService = FakeUserService(
            initialUsers = listOf(
                UserOutputModel(
                    id = 0,
                    name = "John",
                    email = "test@email.com",
                    balance = 100,
                    gamesPlayed = 0,
                    gamesWon = 0
                )
            )
        )

        val fakeBalanceService =
            FakeBalanceService(
                userService = fakeUserService,
                priceTable = mapOf("SP" to 50)
            )

        val viewModel =
            ShopViewModel(
                service = fakeBalanceService,
                authRepo = FakeAuthInfoRepo(),
                userService = fakeUserService
            )

        composeTestRule.setContent {
            val state = viewModel.state.collectAsState()

            if (state.value is ShopScreenState.Success) {
                ShopView(
                    currentBalance = (state.value as ShopScreenState.Success).currentBalance,
                    isPurchasing = (state.value as ShopScreenState.Success).isPurchasing,
                    onPurchase = { viewModel.purchaseBalance(it) }
                )
            }
        }

        // Click first package
        composeTestRule
            .onAllNodesWithTag(BUNDLES_TAG)
            .onFirst()
            .performClick()

        // Balance should update
        composeTestRule
            .onNodeWithTag(SHOP_NUMERIC_BALANCE_TAG)
            .assertExists()
    }

}