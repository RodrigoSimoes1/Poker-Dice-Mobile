package com.example.myapplication.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.myapplication.commonElements.INFO_BUTTON_TAG
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.profile.model.UserOutputModel
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profile_error_state_is_displayed_when_no_session_exists() {
        val viewModel =
            ProfileViewModel(
                service = FakeUserService(),
                authRepo = FakeAuthInfoRepo(initialAuthInfo = null), // no auth info
                logoutUseCase = FakeLogoutUseCase()
            )

        composeTestRule.setContent {
            ProfileScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(PROFILE_SCAFFOLD_SCREEN)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(PROFILE_MAIN_BOX)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(TRY_AGAIN_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun profile_success_state_is_displayed_when_user_is_loaded() {
        val user =
            UserOutputModel(
                id = 1,
                name = "John Test",
                email = "john@test.com",
                balance = 100,
                gamesPlayed = 10,
                gamesWon = 5
            )

        val viewModel =
            ProfileViewModel(
                service = FakeUserService(users = listOf(user)),
                authRepo =
                    FakeAuthInfoRepo(
                        AuthInfo(
                            userId = 1,
                            userEmail = "john@test.com",
                            authToken = "token"
                        )
                    ),
                logoutUseCase = FakeLogoutUseCase()
            )

        composeTestRule.setContent {
            ProfileScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(PROFILE_SCAFFOLD_SCREEN)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(PROFILE_MAIN_BOX)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(PROFILE_VIEW_TAG)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun clicking_try_again_does_not_crash_and_keeps_error_state() {
        val viewModel =
            ProfileViewModel(
                service = FakeUserService(),
                authRepo = FakeAuthInfoRepo(initialAuthInfo = null),
                logoutUseCase = FakeLogoutUseCase()
            )

        composeTestRule.setContent {
            ProfileScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithTag(TRY_AGAIN_TAG)
            .performClick()

        // Still in error state
        composeTestRule
            .onNodeWithTag(TRY_AGAIN_TAG)
            .assertExists()
    }


    @Test
    fun clicking_generate_invite_triggers_invite_generation() {
        val user =
            UserOutputModel(
                id = 1,
                name = "John Test",
                email = "john@test.com",
                balance = 100,
                gamesPlayed = 10,
                gamesWon = 5
            )

        val viewModel =
            ProfileViewModel(
                service = FakeUserService(users = listOf(user)),
                authRepo =
                    FakeAuthInfoRepo(
                        AuthInfo(
                            userId = 1,
                            userEmail = "john@test.com",
                            authToken = "token"
                        )
                    ),
                logoutUseCase = FakeLogoutUseCase()
            )

        composeTestRule.setContent {
            ProfileScreen(viewModel = viewModel)
        }

        // Generate invite button exists
        composeTestRule
            .onNodeWithTag(NO_INVITE_CODE_BUTTON_TAG)
            .assertExists()
            .performClick()

        // Invite code UI appears
        composeTestRule
            .onNodeWithTag(INVITE_CODE_INFO_TAG)
            .assertExists()
    }

    @Test
    fun clicking_logout_triggers_navigate_to_login() {
        val user =
            UserOutputModel(
                id = 1,
                name = "John Test",
                email = "john@test.com",
                balance = 100,
                gamesPlayed = 10,
                gamesWon = 5
            )

        var navigationIntent: ProfileScreenNavigationIntent? = null

        val viewModel =
            ProfileViewModel(
                service = FakeUserService(users = listOf(user)),
                authRepo =
                    FakeAuthInfoRepo(
                        AuthInfo(
                            userId = 1,
                            userEmail = "john@test.com",
                            authToken = "token"
                        )
                    ),
                logoutUseCase = FakeLogoutUseCase()
            )

        composeTestRule.setContent {
            ProfileScreen(
                viewModel = viewModel,
                onNavigate = { navigationIntent = it }
            )
        }

        composeTestRule
            .onNodeWithTag(LOGOUT_BUTTON_TEST_TAG)
            .assertExists()
            .performClick()

        assert(navigationIntent == ProfileScreenNavigationIntent.NavigateToLogin)
    }

    @Test
    fun clicking_back_button_navigates_to_home() {
        var navigationIntent: ProfileScreenNavigationIntent? = null

        val viewModel =
            ProfileViewModel(
                service = FakeUserService(),
                authRepo = FakeAuthInfoRepo(null),
                logoutUseCase = FakeLogoutUseCase()
            )

        composeTestRule.setContent {
            ProfileScreen(
                viewModel = viewModel,
                onNavigate = { navigationIntent = it }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(navigationIntent == ProfileScreenNavigationIntent.NavigateToHome)
    }

    @Test
    fun clicking_info_button_navigates_to_about() {
        var navigationIntent: ProfileScreenNavigationIntent? = null

        val viewModel =
            ProfileViewModel(
                service = FakeUserService(),
                authRepo = FakeAuthInfoRepo(null),
                logoutUseCase = FakeLogoutUseCase()
            )

        composeTestRule.setContent {
            ProfileScreen(
                viewModel = viewModel,
                onNavigate = { navigationIntent = it }
            )
        }

        composeTestRule
            .onNodeWithTag(INFO_BUTTON_TAG)
            .performClick()

        assert(navigationIntent == ProfileScreenNavigationIntent.NavigateToAbout)
    }




}
