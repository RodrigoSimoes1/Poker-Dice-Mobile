package com.example.myapplication.lobbies

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.Dimensions

const val IDLE_STATE_TAG = "idle_state"
const val LOBBIES_VIEW_TAG = "lobbies_view"
const val CREATE_LOBBY_TAG = "create_lobby"
const val RETRY_BUTTON_TAG = "retry_button"
const val WAITING_ROOM_TAG = "waiting_room"
const val ENTERING_LOBBY_INDICATOR_TAG = "entering_lobby_indicator"
const val LEAVE_OR_CLOSE_LOBBY_TEXT_TAG = "leave_or_close_lobby_text"
const val LEAVING_LOBBY_BUTTON_TAG = "leaving_lobby_button"
const val CANCEL_CLOSE_LOBBY_BUTTON_TAG = "cancel_close_lobby_button"


enum class LobbiesNavigationIntent {
    Back,
    About,
    EnterMatch,
}

@Composable
fun LobbiesSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: LobbiesViewModel,
    onNavigate: (LobbiesNavigationIntent) -> Unit = { },
) {
    val state by viewModel.currentState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startRealtimeUpdates()
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = state is LobbiesScreenState.WaitingRoom || state is LobbiesScreenState.CreatingLobby) {
        when (state) {
            is LobbiesScreenState.CreatingLobby -> viewModel.onCancelCreation()

            is LobbiesScreenState.WaitingRoom -> showExitDialog = true

            else -> { }
        }
    }

    if (showExitDialog && state is LobbiesScreenState.WaitingRoom) {
        val lobbyState = state as LobbiesScreenState.WaitingRoom
        val amIHost = lobbyState.lobby.host.email == lobbyState.currentUserId

        AlertDialog(
            modifier = Modifier.testTag(LEAVE_OR_CLOSE_LOBBY_TEXT_TAG),
            onDismissRequest = { showExitDialog = false },
            title = { Text(if (amIHost) stringResource(id = R.string.close_lobby_text) else stringResource(id = R.string.leave_lobby_text)) },
            text = {
                Text(
                    if (amIHost) {
                        stringResource(id = R.string.host_leaving_text)
                    } else {
                        stringResource(id = R.string.confirm_going_back_to_the_list_text)
                    },
                )
            },
            confirmButton = {
                Button(
                    modifier = Modifier.testTag(LEAVING_LOBBY_BUTTON_TAG),
                    onClick = {
                        showExitDialog = false
                        viewModel.leaveLobby(lobbyState.lobby.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Text(stringResource(id = R.string.leave_text))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }, modifier = Modifier.testTag(CANCEL_CLOSE_LOBBY_BUTTON_TAG)) {
                    Text(stringResource(id = R.string.cancel_text))
                }
            },
        )
    }

    when (state) {
        is LobbiesScreenState.Idle -> {
            Box(
                modifier = modifier.fillMaxSize().testTag(IDLE_STATE_TAG),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is LobbiesScreenState.Loading -> {
            val loadingState = state as LobbiesScreenState.Loading

            if (loadingState.message != null) {
                Toast
                    .makeText(
                        LocalContext.current,
                        loadingState.message,
                        Toast.LENGTH_LONG,
                    ).show()
            }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is LobbiesScreenState.LobbiesSelection -> {
            LobbiesView(
                modifier = modifier.testTag(LOBBIES_VIEW_TAG),
                onNavigate = onNavigate,
                viewModel = viewModel,
                lobbies = (state as LobbiesScreenState.LobbiesSelection).lobbies,
                onCreateClick = { viewModel.startCreatingLobby() },
                currentUserId = (state as LobbiesScreenState.LobbiesSelection).currentUserId,
            )
        }

        is LobbiesScreenState.CreatingLobby -> {
            CreateLobbyView(
                modifier = modifier.testTag(CREATE_LOBBY_TAG),
                lobbyData = (state as LobbiesScreenState.CreatingLobby).lobby,
                viewModel = viewModel,
                onNavigate = { navIntent ->
                    when (navIntent) {
                        LobbiesNavigationIntent.Back ->
                            viewModel.onCancelCreation()

                        LobbiesNavigationIntent.About ->
                            onNavigate(LobbiesNavigationIntent.About)

                        else -> {}
                    }
                },
            )
        }

        is LobbiesScreenState.Error -> {
            val error = (state as LobbiesScreenState.Error).errorMessage
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spaced16),
                    modifier = Modifier.padding(Dimensions.padding16),
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Erro",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Dimensions.size64),
                    )
                    Text(
                        text = stringResource(id = R.string.text_for_error),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = { viewModel.loadLobbies() },
                        modifier = Modifier.testTag(RETRY_BUTTON_TAG),
                    ) {
                        Text(stringResource(id = R.string.try_again_text))
                    }
                }
            }
        }

        is LobbiesScreenState.WaitingRoom -> {
            val waitingState = state as LobbiesScreenState.WaitingRoom
            WaitingRoomView(
                modifier = modifier.testTag(WAITING_ROOM_TAG),
                lobby = waitingState.lobby,
                currentUserId = waitingState.currentUserId,
                onLeave = { viewModel.leaveLobby(waitingState.lobby.id) },
                onStart = {
                    viewModel.startMatch(waitingState.lobby.id) {
                        onNavigate(LobbiesNavigationIntent.EnterMatch)
                    }
                },
            )
        }
        is LobbiesScreenState.NavigateToMatch -> {
            LaunchedEffect(state) {
                onNavigate(LobbiesNavigationIntent.EnterMatch)
            }

            Box(Modifier.fillMaxSize().testTag(ENTERING_LOBBY_INDICATOR_TAG), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text(stringResource(id = R.string.entering_game_text))
            }
        }
    }
}
