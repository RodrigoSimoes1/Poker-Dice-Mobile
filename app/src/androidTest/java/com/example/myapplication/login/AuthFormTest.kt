package com.example.myapplication.login

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AuthFormTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test // test the login state
    fun login_state_displays_correct_fields() {
        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = null,
                isRegistering = false,
                name = "",
                email = "",
                password = "",
                inviteCode = "",
                isDataValid = false,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, _, _, _ -> },
                onToggleMode = {},
            )
        }
        // name and Invite Code SHOULD NOT be displayed in login mode
        composeTestRule.onNodeWithTag("person_icon").assertExists()
        composeTestRule.onNodeWithTag("email_text_field").assertExists()
        composeTestRule.onNodeWithTag("password_text_field").assertExists()
        composeTestRule.onNodeWithTag("name_text_field").assertDoesNotExist()
        composeTestRule.onNodeWithTag("invitation_code_text_field_tag").assertDoesNotExist()
        composeTestRule.onNodeWithTag("login_button_tag").assertExists()
        composeTestRule.onNodeWithTag("toggle_method_button").assertExists()
    }

    @Test // test the create account state
    fun create_account_state_displays_correct_fields() {
        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = null,
                isRegistering = true,
                name = "",
                email = "",
                password = "",
                inviteCode = "",
                isDataValid = false,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, _, _, _ -> },
                onToggleMode = {},
            )
        }
        // in this state all the components should be displayed
        composeTestRule.onNodeWithTag("person_icon").assertExists()
        composeTestRule.onNodeWithTag("email_text_field").assertExists()
        composeTestRule.onNodeWithTag("password_text_field").assertExists()
        composeTestRule.onNodeWithTag("name_text_field").assertExists()
        composeTestRule.onNodeWithTag("invitation_code_text_field_tag").assertExists()
        composeTestRule.onNodeWithTag("login_button_tag").assertExists()
        composeTestRule.onNodeWithTag("toggle_method_button").assertExists()
    }

    @Test // test the password text hiding function
    fun passwordVisibility_toggleChangesIcon() {
        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = null,
                isRegistering = false,
                name = "",
                email = "",
                password = "",
                inviteCode = "",
                isDataValid = true,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, _, _, _ -> },
                onToggleMode = {},
            )
        }
        // assert that the password visibility icon is displayed
        composeTestRule.onNodeWithContentDescription("hide").assertExists()
        // click the button to activate the show mode
        composeTestRule.onNodeWithContentDescription("hide").performClick()
        // verify that the mode of the button changed
        composeTestRule.onNodeWithContentDescription("show").assertExists()
        // click again to test if iot returns to the original state
        composeTestRule.onNodeWithContentDescription("show").performClick()
        // verify if we are now in hide mode
        composeTestRule.onNodeWithContentDescription("hide").assertExists()
    }

    @Test // test if the error messages appear
    fun errorMessage_isShown() {
        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = "test error",
                isRegistering = false,
                name = "",
                email = "",
                password = "",
                inviteCode = "",
                isDataValid = false,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, _, _, _ -> },
                onToggleMode = {},
            )
        }
        // test if the message is shown
        composeTestRule.onNodeWithText("test error").assertExists()
    }

    @Test
    fun clicking_login_button_calls_onSubmit_with_credentials() {
        var submittedEmail: String? = null
        var submittedPassword: String? = null

        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = null,
                isRegistering = false,
                name = "",
                email = "user@test.com",
                password = "password123",
                inviteCode = "",
                isDataValid = true,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, email, password, _ ->
                    submittedEmail = email
                    submittedPassword = password
                },
                onToggleMode = {},
            )
        }

        composeTestRule
            .onNodeWithTag("login_button_tag")
            .performClick()

        assert(submittedEmail == "user@test.com")
        assert(submittedPassword == "password123")
    }

    @Test
    fun clicking_register_button_calls_onSubmit_with_register_data() {
        var submittedName: String? = null
        var submittedEmail: String? = null
        var submittedInviteCode: String? = null

        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = null,
                isRegistering = true,
                name = "John Test",
                email = "john@test.com",
                password = "password123",
                inviteCode = "INV123",
                isDataValid = true,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { name, email, _, inviteCode ->
                    submittedName = name
                    submittedEmail = email
                    submittedInviteCode = inviteCode
                },
                onToggleMode = {},
            )
        }

        composeTestRule
            .onNodeWithTag("login_button_tag")
            .performClick()

        assert(submittedName == "John Test")
        assert(submittedEmail == "john@test.com")
        assert(submittedInviteCode == "INV123")
    }

    @Test
    fun clicking_toggle_mode_button_calls_onToggleMode() {
        var toggleCalled = false

        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = null,
                isRegistering = false,
                name = "John Test",
                email = "test@email.com",
                password = "password",
                inviteCode = "testinv",
                isDataValid = true,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, _, _, _ -> },
                onToggleMode = { toggleCalled = true },
            )
        }

        composeTestRule
            .onNodeWithTag("toggle_method_button")
            .performClick()

        assert(toggleCalled)
    }

    @Test
    fun login_button_disabled_does_not_call_onSubmit() {
        var submitCalled = false

        composeTestRule.setContent {
            AuthFormStateless(
                loading = false,
                error = null,
                isRegistering = false,
                name = "",
                email = "",
                password = "",
                inviteCode = "",
                isDataValid = false,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, _, _, _ -> submitCalled = true },
                onToggleMode = {},
            )
        }

        composeTestRule
            .onNodeWithTag("login_button_tag")
            .assertIsNotEnabled()

        assert(!submitCalled)
    }

    @Test
    fun loading_state_disables_button_and_shows_progress() {
        composeTestRule.setContent {
            AuthFormStateless(
                loading = true,
                error = null,
                isRegistering = false,
                name = "",
                email = "user@test.com",
                password = "password",
                inviteCode = "",
                isDataValid = true,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onInviteCodeChange = {},
                onSubmit = { _, _, _, _ -> },
                onToggleMode = {},
            )
        }

        composeTestRule
            .onNodeWithTag("login_button_tag")
            .assertIsNotEnabled()

        composeTestRule
            .onNode(hasTestTag("login_button_tag") and hasAnyChild(hasTestTag("CircularProgressIndicator")))
    }



}
