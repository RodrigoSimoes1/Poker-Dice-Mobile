package com.example.myapplication.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.ui.theme.Dimensions
import com.example.myapplication.R

const val PROFILE_SCAFFOLD_SCREEN = "PROFILE_SCAFFOLD_SCREEN"
const val PROFILE_MAIN_BOX = "PROFILE_MAIN_BOX"
const val PROFILE_CIRCULAR_PROGRESS_TAG = "PROFILE_CIRCULAR_PROGRESS_TAG"
const val TRY_AGAIN_TAG = "TRY_AGAIN_TAG"
const val PROFILE_VIEW_TAG = "PROFILE_VIEW_TAG"

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    onNavigate: (ProfileScreenNavigationIntent) -> Unit = { },
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                onBackIntent = { onNavigate(ProfileScreenNavigationIntent.NavigateToHome) },
                onInfoIntent = { onNavigate(ProfileScreenNavigationIntent.NavigateToAbout) },
            )
        },
        modifier = modifier.testTag(PROFILE_SCAFFOLD_SCREEN)
    ) { padding ->
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Dimensions.screenPadding)
                    .testTag(PROFILE_MAIN_BOX),
            contentAlignment = Alignment.Center,
        ) {
            when (val currentState = state) {
                is ProfileScreenState.Idle,
                is ProfileScreenState.Loading,
                -> {
                    CircularProgressIndicator(modifier = Modifier.testTag(PROFILE_CIRCULAR_PROGRESS_TAG))
                }
                is ProfileScreenState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentState.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadUserByEmail() }, modifier = Modifier.testTag(TRY_AGAIN_TAG)) {
                            Text(stringResource(R.string.text_to_try_again))
                        }
                    }
                }
                is ProfileScreenState.Success -> {
                    ProfileView(
                        name = currentState.user.name,
                        gamesPlayed = currentState.user.gamesPlayed,
                        gamesWon = currentState.user.gamesWon,
                        balance = currentState.user.balance,
                        onNavigate = { intent -> onNavigate(intent) },
                        inviteCode = (state as ProfileScreenState.Success).inviteCode?.code,
                        onGenerateInvite = { viewModel.generateInvite() },
                        modifier = Modifier.testTag(PROFILE_VIEW_TAG)
                    )
                }
            }
        }
    }
}
