package com.example.myapplication.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.login.domain.isValidCredentialsData
import com.example.myapplication.login.domain.isValidRegisterData
import com.example.myapplication.R
import com.example.myapplication.ui.theme.Dimensions

const val AUTH_SCREEN_SCAFFOLD_TAG = "auth_screen_scaffold"
const val AUTH_SCREEN_MAIN_BOX_TAG = "auth_screen_main_box"
const val AUTH_SCREEN_AUTHFORM_TAG = "auth_screen_authform"

enum class AuthScreenNavigationIntent {
    NavigateBack,
    AboutScreenNavigationIntent,
    MainScreenNavigationIntent,
}

/**
 * Composable function representing the Authentication Screen (Login/Register).
 */
@Composable
fun AuthScreen(
    modifier: Modifier,
    onNavigate: (AuthScreenNavigationIntent) -> Unit = { },
    viewModel: AuthViewModel,
) {
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.auth_title_authentication_text),
                onInfoIntent = { onNavigate(AuthScreenNavigationIntent.AboutScreenNavigationIntent) },
            )
        },
        modifier = Modifier.fillMaxSize().testTag(AUTH_SCREEN_SCAFFOLD_TAG),
    ) { innerPadding ->

        val observedState by viewModel.currentState.collectAsState(AuthScreenState.Idle)

        LaunchedEffect(observedState) {
            if (observedState is AuthScreenState.LoginSuccess || observedState is AuthScreenState.RegisterSuccess) {
                onNavigate(AuthScreenNavigationIntent.MainScreenNavigationIntent)
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimensions.horizontal48)
                    .imePadding()
                    .testTag(AUTH_SCREEN_MAIN_BOX_TAG),
        ) {
            AuthForm(
                loading = observedState is AuthScreenState.LoginInProgress,
                error =
                    if (observedState is AuthScreenState.AuthError) {
                        (observedState as AuthScreenState.AuthError).errorMessage
                    } else {
                        null
                    },
                onLogin = { credentials -> viewModel.login(credentials) },
                onRegister = { data -> viewModel.register(data) },
                validateCredentials = ::isValidCredentialsData,
                validateRegister = ::isValidRegisterData,
                modifier = Modifier.testTag(AUTH_SCREEN_AUTHFORM_TAG),
            )
        }
    }
}
