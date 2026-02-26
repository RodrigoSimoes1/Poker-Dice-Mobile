package com.example.myapplication.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.login.domain.RegisterData
import com.example.myapplication.login.domain.UserCredentials
import com.example.myapplication.ui.theme.Dimensions
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun AuthForm(
    loading: Boolean,
    error: String?,
    onLogin: (credentials: UserCredentials) -> Unit,
    onRegister: (data: RegisterData) -> Unit,
    validateCredentials: (email: String, password: String) -> Boolean,
    validateRegister: (name: String, email: String, password: String, inviteCode: String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    var isRegistering by rememberSaveable { mutableStateOf(false) }

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var inviteCode by rememberSaveable { mutableStateOf("") }

    val isDataValid =
        if (isRegistering) {
            validateRegister(name, email, password, inviteCode)
        } else {
            validateCredentials(email, password)
        }

    AuthFormStateless(
        loading = loading,
        error = error,
        isRegistering = isRegistering,
        name = name,
        email = email,
        password = password,
        inviteCode = inviteCode,
        isDataValid = isDataValid,
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onInviteCodeChange = { inviteCode = it },
        onSubmit = { name, email, password, inviteCode ->
            if (isRegistering) {
                onRegister(RegisterData(name, email, password, inviteCode))
            } else {
                onLogin(UserCredentials(email, password))
            }
        },
        onToggleMode = { isRegistering = !isRegistering },
        modifier = modifier,
    )
}

@Composable
fun AuthFormStateless(
    loading: Boolean,
    error: String?,
    isRegistering: Boolean,
    name: String,
    email: String,
    password: String,
    inviteCode: String,
    isDataValid: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onInviteCodeChange: (String) -> Unit,
    onSubmit: (name: String, email: String, password: String, inviteCode: String) -> Unit,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacingVertical),
        modifier =
            modifier
                .fillMaxSize()
                .padding(Dimensions.screenPadding)
                .verticalScroll(rememberScrollState()),
    ) {
        // Title(if (isRegistering) R.string.register_screen_title else R.string.login_screen_title)

        Image(
            painter = painterResource(id = R.drawable.person_icon),
            contentDescription = "person_icon",
            modifier = Modifier.size(Dimensions.iconSizeLarge).testTag("person_icon"),
        )

        if (isRegistering) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(id = R.string.name_text)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("name_text_field"),
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email_button_text)) },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            modifier = Modifier.fillMaxWidth().testTag("email_text_field"),
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(id = R.string.auth_password_text)) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "show" else "hide"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("password_text_field"),
        )

        if (isRegistering) {
            OutlinedTextField(
                value = inviteCode,
                onValueChange = onInviteCodeChange,
                label = { Text(stringResource(id = R.string.invitation_code_text)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("invitation_code_text_field_tag"),
            )
        }

        if (!error.isNullOrBlank()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { onSubmit(name, email, password, inviteCode) },
            enabled = isDataValid && !loading,
            modifier = Modifier.fillMaxWidth().testTag("login_button_tag"),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    Modifier.size(Dimensions.size16).padding(end = Dimensions.padding8),
                    strokeWidth = Dimensions.strokeWidth2,
                )
            }
            Text(
                if (isRegistering) {
                    stringResource(
                        id = R.string.create_account_text,
                    )
                } else {
                    stringResource(id = R.string.enter_account_title_text)
                },
            )
        }

        TextButton(
            onClick = onToggleMode,
            modifier = Modifier.fillMaxWidth().testTag("toggle_method_button"),
        ) {
            Text(
                if (isRegistering) {
                    stringResource(id = R.string.existing_account_text)
                } else {
                    stringResource(id = R.string.no_existing_account_text)
                },
            )
        }
    }
}

@Preview(name = "Login Form", showBackground = true, showSystemUi = true)
@Composable
fun AuthFormLoginPreview() {
    MyApplicationTheme {
        AuthFormStateless(
            loading = false,
            error = null,
            isRegistering = false,
            name = "",
            email = "user@example.com",
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
}

@Preview(name = "Register Form", showBackground = true, showSystemUi = true)
@Composable
fun AuthFormRegisterPreview() {
    MyApplicationTheme {
        AuthFormStateless(
            loading = false,
            error = null,
            isRegistering = true,
            name = "Rodrigo_goat",
            email = "Rodrigo_goat@exemplo.pt",
            password = "password123",
            inviteCode = "INV123",
            isDataValid = true,
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onInviteCodeChange = {},
            onSubmit = { _, _, _, _ -> },
            onToggleMode = {},
        )
    }
}
