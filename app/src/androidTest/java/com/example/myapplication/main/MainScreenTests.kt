package com.example.myapplication.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.commonElements.INFO_BUTTON_TAG
import com.example.myapplication.commonElements.PROFILE_BUTTON_TAG
import com.example.myapplication.commonElements.TITLE_TEXT_TAG
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun clicking_InfoButton_triggersNavigateToAboutIntent() {
        var navigationIntent: MainScreenNavigationIntent? = null
        composeTestRule.setContent {
            MainScreen(
                onNavigate = { navigationIntent = it },
            )
        }

        composeTestRule.onNodeWithTag(testTag = INFO_BUTTON_TAG).performClick()
        assert(navigationIntent == MainScreenNavigationIntent.NavigateToAbout)
    }

    @Test
    fun clicking_ProfileButton_triggersNavigateToAboutIntent() {
        var navigationIntent: MainScreenNavigationIntent? = null
        composeTestRule.setContent {
            MainScreen(
                onNavigate = { navigationIntent = it },
            )
        }

        composeTestRule.onNodeWithTag(testTag = PROFILE_BUTTON_TAG).performClick()
        assert(navigationIntent == MainScreenNavigationIntent.NavigateToProfile)
    }

    @Test
    fun clicking_CrowdTallyButton_triggersNavigateToLobbysIntent() {
        var navigationIntent: MainScreenNavigationIntent? = null
        composeTestRule.setContent {
            MainScreen(
                onNavigate = { navigationIntent = it },
            )
        }

        composeTestRule.onNodeWithTag(testTag = START_BUTTON_TAG).performClick()
        assert(navigationIntent == MainScreenNavigationIntent.NavigateToLobbys)
    }

    @Test
    fun mainScreen_rendersAllButtonsAndTitle() {
        composeTestRule.setContent {
            MainScreen(onNavigate = {})
        }

        composeTestRule.onNodeWithTag(testTag = INFO_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(testTag = PROFILE_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(testTag = START_BUTTON_TAG).assertExists()
        composeTestRule.onNodeWithTag(testTag = TITLE_TEXT_TAG).assertExists()
    }
}
