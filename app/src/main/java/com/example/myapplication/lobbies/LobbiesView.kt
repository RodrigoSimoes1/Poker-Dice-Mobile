package com.example.myapplication.lobbies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.ui.theme.Dimensions
import com.example.myapplication.R

const val LOBBIES_SCAFFOLD_TAG = "LOBBIES_SCAFFOLD_TAG"
const val CREATE_LOBBY_BUTTON_TAG = "CREATE_LOBBY_BUTTON_TAG"
const val REFRESH_BUTTON_TAG = "REFRESH_BUTTON_TAG"
const val NO_LOBBIES_BOX_TAG = "NO_LOBBIES_BOX_TAG"
const val NO_LOBBIES_TEXT_TAG = "NO_LOBBIES_TEXT_TAG"
const val LOBBIES_AVAILABLE_TAG = "LOBBIES_AVAILABLE_TAG"
const val MAXLINES = 2
const val N = 4
const val MAX_LINE = 1

@Composable
fun LobbiesView(
    modifier: Modifier = Modifier,
    onNavigate: (LobbiesNavigationIntent) -> Unit = { },
    viewModel: LobbiesViewModel,
    lobbies: List<LobbyOutputModel>,
    currentUserId: Int,
    onCreateClick: () -> Unit,
) {
    val visibleLobbies =
        remember(lobbies, currentUserId) {
            lobbies.filter { lobby ->
                lobby.isPublic || lobby.players.any { it.id == currentUserId }
            }
        }

    Scaffold(
        modifier = Modifier.testTag(LOBBIES_SCAFFOLD_TAG),
        topBar = {
            TopBar(
                title = stringResource(id = R.string.lobbies_title_text),
                onBackIntent = { onNavigate(LobbiesNavigationIntent.Back) },
                onInfoIntent = { onNavigate(LobbiesNavigationIntent.About) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag(CREATE_LOBBY_BUTTON_TAG),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Criar Lobby",
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Dimensions.screenPadding),
        ) {
            OutlinedButton(
                onClick = { viewModel.loadLobbies() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimensions.padding16)
                        .testTag(REFRESH_BUTTON_TAG),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar",
                    modifier = Modifier.padding(end = Dimensions.padding8),
                )
                Text(stringResource(id = R.string.update_lobbies_list))
            }

            Text(
                text = stringResource(id = R.string.list_of_available_lobbies),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

            if (visibleLobbies.isEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .weight(WEIGHT)
                            .testTag(NO_LOBBIES_BOX_TAG),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spaced8),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.size48),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(id = R.string.no_lobbies_visible),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag(NO_LOBBIES_TEXT_TAG),

                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spaced16),
                    contentPadding = PaddingValues(bottom = Dimensions.padding80),
                    modifier = Modifier.fillMaxSize().testTag(LOBBIES_AVAILABLE_TAG),
                ) {
                    items(visibleLobbies) { lobby ->
                        val amIInLobby =
                            lobby.players.any {
                                it.id == currentUserId
                            }

                        LobbyCard(
                            lobby = lobby,
                            currentUserId = currentUserId,
                            onJoin = {
                                if (amIInLobby) {
                                    viewModel.returnToLobby(lobby)
                                } else {
                                    viewModel.joinLobby(lobby.id)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LobbyCard(
    lobby: LobbyOutputModel,
    currentUserId: Int,
    onJoin: () -> Unit,
    testTag:String = "Default"
) {
    val amIInLobby =
        lobby.players.any {
            it.id == currentUserId
        }

    Card(
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.padding16),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = lobby.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                if (lobby.isPublic) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(Dimensions.roundedCornerShape12),
                    ) {
                        Text(
                            text = stringResource(id = R.string.public_text),
                            modifier = Modifier.padding(horizontal = Dimensions.padding8, vertical = Dimensions.padding4),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(Dimensions.roundedCornerShape12),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = Dimensions.padding8, vertical = Dimensions.padding4),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(Dimensions.size12),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                            Spacer(Modifier.width(Dimensions.width4))
                            Text(
                                text = stringResource(id = R.string.private_text),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.height8))

            if (lobby.description.isNotBlank()) {
                Text(
                    text = lobby.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = MAXLINES,
                )
                Spacer(modifier = Modifier.height(Dimensions.height12))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Host",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimensions.size18),
                    )
                    Spacer(modifier = Modifier.width(Dimensions.width4))
                    Text(
                        text = lobby.host.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Jogadores",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(Dimensions.size18),
                    )
                    Spacer(modifier = Modifier.width(Dimensions.width4))
                    Text(
                        text = "${lobby.players.size}/${lobby.maxPlayers}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.height8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rondas",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(Dimensions.size18),
                    )
                    Spacer(modifier = Modifier.width(Dimensions.width4))
                    Text(
                        text = "${lobby.numberOfRounds} " + stringResource(id = R.string.rounds_text),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Custo",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Dimensions.size18),
                    )
                    Spacer(modifier = Modifier.width(Dimensions.width4))
                    Text(
                        text = "${lobby.costToPlay}€",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.height16))

            if (lobby.players.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.in_the_room_text),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(Dimensions.height4))

                    lobby.players.take(N).forEach { player ->
                        Text(
                            text = "• ${player.name}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = MAX_LINE,
                            modifier = Modifier.padding(start = Dimensions.padding4, bottom = Dimensions.padding2),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimensions.height16))
            }

            Button(
                onClick = onJoin,
                modifier = Modifier.fillMaxWidth(),
                enabled = amIInLobby || lobby.players.size < lobby.maxPlayers,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (amIInLobby) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                    ),
            ) {
                Icon(
                    imageVector = if (amIInLobby) Icons.AutoMirrored.Filled.Login else Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Dimensions.padding8),
                )
                Text(
                    text =
                        when {
                            amIInLobby -> stringResource(id = R.string.return_to_lobby_text)
                            lobby.players.size >= lobby.maxPlayers -> stringResource(id = R.string.lobby_full_text)
                            else -> stringResource(id = R.string.text_to_enter_the_lobby)
                        },
                )
            }
        }
    }
}
