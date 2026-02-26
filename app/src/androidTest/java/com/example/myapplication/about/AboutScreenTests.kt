package com.example.myapplication.about

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.myapplication.commonElements.BACK_BUTTON_TAG
import org.junit.Rule
import org.junit.Test

class AboutScreenTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun aboutScreen_rendersTextAndButtons() {
        composeTestRule.setContent {
            AboutScreen()
        }

        composeTestRule.onNodeWithTag(RULES_SECTION_TAG).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(AUTHOR_SECTION_TAG).assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag(BACK_BUTTON_TAG).assertExists().assertIsDisplayed()
    }

    @Test
    fun clickingAuthorSection_triggersEmailIntent() {
        var navigationIntent: AboutScreenNavigationIntent? = null

        composeTestRule.setContent {
            AboutScreen(onNavigate = { navigationIntent = it })
        }

        composeTestRule.onNodeWithTag(AUTHOR_SECTION_TAG).performClick()
        assert(navigationIntent is AboutScreenNavigationIntent.Email)
        assert((navigationIntent as AboutScreenNavigationIntent.Email).destination.contentEquals(EMAILS))
        assert((navigationIntent as AboutScreenNavigationIntent.Email).subject == SUBJECT)
    }

    @Test
    fun clickingRulesSection_triggersBrowserIntent() {
        var navigationIntent: AboutScreenNavigationIntent? = null

        composeTestRule.setContent {
            AboutScreen(onNavigate = { navigationIntent = it })
        }

        composeTestRule.onNodeWithTag(RULES_SECTION_TAG).performClick()
        assert(navigationIntent is AboutScreenNavigationIntent.Browser)
        assert((navigationIntent as AboutScreenNavigationIntent.Browser).destination == RULES_URL)
    }

    @Test
    fun clickingBackButton_triggersNavigateToHome() {
        var navigationIntent: AboutScreenNavigationIntent? = null

        composeTestRule.setContent {
            AboutScreen(onNavigate = { navigationIntent = it })
        }

        composeTestRule.onNodeWithTag(BACK_BUTTON_TAG).performClick()
        assert(navigationIntent == AboutScreenNavigationIntent.NavigateBack)
    }

    @Test
    fun aboutScreen_initialState_noNavigationTriggered() {
        var navigationIntent: AboutScreenNavigationIntent? = null

        composeTestRule.setContent {
            AboutScreen(onNavigate = { navigationIntent = it })
        }

        assert(navigationIntent == null)
    }
}
