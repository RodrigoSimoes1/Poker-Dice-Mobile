package com.example.myapplication.lobbies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.myapplication.R
import com.example.myapplication.ui.theme.Dimensions

const val WAITING_ROOM_SCAFFOLD_TAG = "WAITING_ROOM_SCAFFOLD_TAG"
const val LOBBY_CARD_TAG = "LOBBY_CARD_TAG"
const val PLAYERS_COLUMN_TAG = "PLAYERS_COLUMN_TAG"
const val START_GAME_BUTTON_TAG = "START_GAME_BUTTON_TAG"
const val LEAVE_LOBBY_BUTTON_TAG = "LEAVE_LOBBY_BUTTON_TAG"
const val WAITING_TAG = "WAITING_TAG"



@Composable
fun WaitingRoomView(
    modifier: Modifier = Modifier,
    lobby: LobbyOutputModel,
    currentUserId: String,
    onLeave: () -> Unit,
    onStart: () -> Unit,
) {
    val amIHost = lobby.host.email == currentUserId

    Scaffold(
        topBar = {
            TopBar(
                title = lobby.name,
            )
        },
        modifier = Modifier.testTag(WAITING_ROOM_SCAFFOLD_TAG),
    ) { padding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Dimensions.padding16),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spaced16),
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().testTag(LOBBY_CARD_TAG),
                ) {
                    Column(modifier = Modifier.padding(Dimensions.padding16)) {
                        Text(stringResource(R.string.host_text) + " ${lobby.host.name}", style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.rounds_with_dots_text) + " ${lobby.numberOfRounds}", style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.cost_text) + " ${lobby.costToPlay}€", style = MaterialTheme.typography.bodyMedium)
                        if (lobby.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(Dimensions.height8))
                            Text(lobby.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.players_text) + " (${lobby.players.size}/${lobby.maxPlayers})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spaced8),
                    modifier = Modifier.testTag(PLAYERS_COLUMN_TAG)
                ) {
                    items(lobby.players) { player ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Dimensions.roundedCornerShape8))
                                    .border(Dimensions.width1, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(Dimensions.roundedCornerShape8))
                                    .padding(Dimensions.padding12),
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(Dimensions.width12))
                            Text(
                                text = if (player.email == currentUserId) "${player.name} " + stringResource(id = R.string.parenthesis_you_text) else player.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (player.email == currentUserId) FontWeight.Bold else FontWeight.Normal,
                            )
                            if (player.email == lobby.host.email) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.Star, contentDescription = "Host", tint = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spaced12),
            ) {
                if (amIHost) {
                    Button(
                        onClick = { onStart() },
                        modifier = Modifier.fillMaxWidth().height(Dimensions.height50).testTag(START_GAME_BUTTON_TAG),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Text(stringResource(id = R.string.text_to_start_the_game))
                    }
                } else {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().testTag(WAITING_TAG),
                    ) {
                        Text(stringResource(id = R.string.waiting_for_host_text))
                    }
                }

                Button(
                    onClick = onLeave,
                    modifier = Modifier.fillMaxWidth().testTag(LEAVE_LOBBY_BUTTON_TAG),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Text(stringResource(id = R.string.text_to_leave_the_lobby))
                }
            }
        }
    }
}
