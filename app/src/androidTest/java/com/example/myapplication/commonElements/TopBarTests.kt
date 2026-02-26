package com.example.myapplication.commonElements

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class TopBarTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topBar_withOnlyBackNavigation_rendersCorrectly() {
        composeTestRule.setContent {
            TopBar(
                onBackIntent = { },
            )
        }

        composeTestRule.onNodeWithTag(testTag = BACK_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(testTag = INFO_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(testTag = PROFILE_BUTTON_TAG).assertDoesNotExist()
    }

    @Test
    fun topBar_withOnlyInfoNavigation_rendersCorrectly() {
        composeTestRule.setContent {
            TopBar(
                onInfoIntent = { },
            )
        }

        composeTestRule.onNodeWithTag(testTag = BACK_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(testTag = INFO_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(testTag = PROFILE_BUTTON_TAG).assertDoesNotExist()
    }

    @Test
    fun topBar_withOnlyProfileNavigation_rendersCorrectly() {
        composeTestRule.setContent {
            TopBar(
                onProfileIntent = { },
            )
        }

        composeTestRule.onNodeWithTag(testTag = BACK_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(testTag = INFO_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(testTag = PROFILE_BUTTON_TAG).assertExists()
    }

    @Test
    fun topBar_withAllNavigationOptionsAndTitle_rendersCorrectly() {
        val title = "Title"
        composeTestRule.setContent {
            TopBar(
                title = title,
                onBackIntent = { },
                onInfoIntent = { },
                onProfileIntent = {},
            )
        }

        composeTestRule.onNodeWithTag(testTag = BACK_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(testTag = INFO_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(PROFILE_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(testTag = TITLE_TEXT_TAG).assertTextEquals(title)
    }

    @Test
    fun clicking_BackButton_triggersBackIntent() {
        var backClicked = false
        composeTestRule.setContent {
            TopBar(
                onBackIntent = { backClicked = true },
            )
        }

        composeTestRule.onNodeWithTag(testTag = BACK_BUTTON_TAG).performClick()
        assert(backClicked)
    }

    @Test
    fun clicking_InfoButton_triggersInfoIntent() {
        var infoClicked = false
        composeTestRule.setContent {
            TopBar(
                onInfoIntent = { infoClicked = true },
            )
        }
        composeTestRule.onNodeWithTag(testTag = INFO_BUTTON_TAG).performClick()
        assert(infoClicked)
    }

    @Test
    fun clicking_ProfileButton_triggersInfoIntent() {
        var profileClicked = false
        composeTestRule.setContent {
            TopBar(
                onProfileIntent = { profileClicked = true },
            )
        }
        composeTestRule.onNodeWithTag(testTag = PROFILE_BUTTON_TAG).performClick()
        assert(profileClicked)
    }

    @Test
    fun topBar_withoutAnyNavigationOptions_rendersTitleOnly() {
        val title = "Only Title"
        composeTestRule.setContent {
            TopBar(title = title)
        }

        composeTestRule.onNodeWithTag(TITLE_TEXT_TAG).assertTextEquals(title)
        composeTestRule.onNodeWithTag(BACK_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(INFO_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(PROFILE_BUTTON_TAG).assertDoesNotExist()
    }
}
