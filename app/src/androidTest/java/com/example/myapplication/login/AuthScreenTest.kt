package com.example.myapplication.login

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    private fun createViewModel(): AuthViewModel {
        return AuthViewModel(
            loginUseCase = FakeLoginUseCase(),
            registerUseCase = FakeRegisterUseCase(),
            service = FakeAuthService(),
            authRepo = FakeAuthRepo()
        )
    }

    @Test
    fun all_components_are_displayed() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            AuthScreen(
                modifier = Modifier,
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag(AUTH_SCREEN_SCAFFOLD_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(AUTH_SCREEN_MAIN_BOX_TAG)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(AUTH_SCREEN_AUTHFORM_TAG)
            .assertExists()
            .assertIsDisplayed()
    }
}
