package com.example.myapplication.profile

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ProfileViewTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun profileScreen_rendersAllTags_With_Invite() {
        composeTestRule.setContent {
            MaterialTheme {
                ProfileView(
                    name = "John test",
                    gamesPlayed = 5,
                    gamesWon = 5,
                    balance = 500,
                    inviteCode = "TEST123",
                )
            }
        }

        composeTestRule.onNodeWithTag(PROFILE_TITLE_TEST_TAG).assertExists()
        composeTestRule.onNodeWithTag(PROFILE_NAME_TAG).assertExists()

        composeTestRule.onNodeWithTag(PROFILE_CONTROLER_ICON_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_PLAYED_NUM_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_PLAYED_TEXT_TAG).assertExists()

        composeTestRule.onNodeWithTag(PROFILE_TROPHY_ICON_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_WON_NUM_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_WON_TEXT_TAG).assertExists()

        composeTestRule
            .onNodeWithTag(PROFILE_MONEY_TAG, useUnmergedTree = true)
            .assertExists()

        composeTestRule.onNodeWithTag(SHOP_CARD_TAG).assertExists()

        composeTestRule.onNodeWithTag(NO_INVITE_CODE_BUTTON_TAG).assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(INVITE_CODE_ICON_TAG, useUnmergedTree = true)
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(GENERATE_INVITE_CODE_TEXT_TAG, useUnmergedTree = true)
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag(INVITE_CODE_INFO_TAG).assertExists()
        composeTestRule.onNodeWithTag(SHOW_INVITE_CODE_TEXT_TAG).assertExists()
        composeTestRule.onNodeWithTag(INVITE_CODE_TAG).assertExists()

        composeTestRule.onNodeWithTag(LOGOUT_BUTTON_TEST_TAG).assertExists()
        composeTestRule
            .onNodeWithTag(LOGOUT_BUTTON_TEXT_TAG, useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun profileScreen_rendersAllTags_Without_Invite() {
        composeTestRule.setContent {
            MaterialTheme {
                ProfileView(
                    name = "John test",
                    gamesPlayed = 5,
                    gamesWon = 5,
                    balance = 500,
                    inviteCode = null,
                )
            }
        }

        composeTestRule.onNodeWithTag(PROFILE_TITLE_TEST_TAG).assertExists()
        composeTestRule.onNodeWithTag(PROFILE_NAME_TAG).assertExists()

        composeTestRule.onNodeWithTag(PROFILE_CONTROLER_ICON_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_PLAYED_NUM_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_PLAYED_TEXT_TAG).assertExists()

        composeTestRule.onNodeWithTag(PROFILE_TROPHY_ICON_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_WON_NUM_TAG).assertExists()
        composeTestRule.onNodeWithTag(GAMES_WON_TEXT_TAG).assertExists()

        composeTestRule
            .onNodeWithTag(PROFILE_MONEY_TAG, useUnmergedTree = true)
            .assertExists()

        composeTestRule.onNodeWithTag(SHOP_CARD_TAG).assertExists()

        composeTestRule.onNodeWithTag(NO_INVITE_CODE_BUTTON_TAG).assertExists()
        composeTestRule
            .onNodeWithTag(INVITE_CODE_ICON_TAG, useUnmergedTree = true)
            .assertExists()
        composeTestRule
            .onNodeWithTag(GENERATE_INVITE_CODE_TEXT_TAG, useUnmergedTree = true)
            .assertExists()

        composeTestRule.onNodeWithTag(INVITE_CODE_INFO_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(SHOW_INVITE_CODE_TEXT_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(INVITE_CODE_TAG).assertDoesNotExist()

        composeTestRule.onNodeWithTag(LOGOUT_BUTTON_TEST_TAG).assertExists()
        composeTestRule
            .onNodeWithTag(LOGOUT_BUTTON_TEXT_TAG, useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun clicking_shop_card_triggers_navigate_to_shop() {
        var navigationIntent: ProfileScreenNavigationIntent? = null

        composeTestRule.setContent {
            MaterialTheme {
                ProfileView(
                    name = "John test",
                    gamesPlayed = 5,
                    gamesWon = 5,
                    balance = 500,
                    inviteCode = "TEST123",
                    onNavigate = { intent -> navigationIntent = intent }
                )
            }
        }

        composeTestRule
            .onNodeWithTag(SHOP_CARD_TAG)
            .assertExists()
            .performClick()

        assert(navigationIntent == ProfileScreenNavigationIntent.NavigateToShop)
    }

    @Test
    fun clicking_generate_invite_button_calls_onGenerateInvite() {
        var generateInviteCalled = false

        composeTestRule.setContent {
            MaterialTheme {
                ProfileView(
                    name = "John test",
                    gamesPlayed = 5,
                    gamesWon = 5,
                    balance = 500,
                    inviteCode = null,
                    onGenerateInvite = { generateInviteCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithTag(NO_INVITE_CODE_BUTTON_TAG)
            .assertExists()
            .performClick()

        assert(generateInviteCalled)
    }

    @Test
    fun clicking_logout_button_triggers_navigate_to_login() {
        var navigationIntent: ProfileScreenNavigationIntent? = null

        composeTestRule.setContent {
            MaterialTheme {
                ProfileView(
                    name = "John test",
                    gamesPlayed = 5,
                    gamesWon = 5,
                    balance = 500,
                    inviteCode = "TEST123",
                    onNavigate = { intent -> navigationIntent = intent }
                )
            }
        }

        composeTestRule
            .onNodeWithTag(LOGOUT_BUTTON_TEST_TAG)
            .assertExists()
            .performClick()

        assert(navigationIntent == ProfileScreenNavigationIntent.NavigateToLogin)
    }

    @Test
    fun when_invite_code_exists_generate_button_is_not_visible() {
        composeTestRule.setContent {
            MaterialTheme {
                ProfileView(
                    name = "John test",
                    gamesPlayed = 5,
                    gamesWon = 5,
                    balance = 500,
                    inviteCode = "INV123"
                )
            }
        }

        composeTestRule.onNodeWithTag(NO_INVITE_CODE_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(INVITE_CODE_INFO_TAG).assertExists()
    }


}
