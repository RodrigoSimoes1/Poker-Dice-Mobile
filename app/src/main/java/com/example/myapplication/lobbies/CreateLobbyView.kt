package com.example.myapplication.lobbies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.lobbies.LobbiesViewModel.Companion.MAX_PLAYERS
import com.example.myapplication.lobbies.LobbiesViewModel.Companion.MAX_ROUNDS
import com.example.myapplication.lobbies.LobbiesViewModel.Companion.MIN_PLAYERS
import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import com.example.myapplication.ui.CounterRow
import com.example.myapplication.R
import com.example.myapplication.about.AboutScreen
import com.example.myapplication.ui.theme.Dimensions

const val CREATE_LOBBY_SCAFFOLD_TAG = "CREATE_LOBBY_SCAFFOLD_TAG"
const val LOBBY_NAME_TEXTFIELD_TAG = "LOBBY_NAME_TEXTFIELD_TAG"
const val LOBBY_DESCRIPTION_TEXTFIELD_TAG = "LOBBY_DESCRIPTION_TEXTFIELD_TAG"
const val PRICE_TEXTFIELD_TAG = "PRICE_TEXTFIELD_TAG"
const val MIN_PLAYERS_COUNTER_TAG = "MIN_PLAYERS_COUNTER_TAG"
const val MAX_PLAYERS_COUNTER_TAG = "MAX_PLAYERS_COUNTER_TAG"
const val NUMBER_OF_ROUNDS_COUNTER_TAG = "NUMBER_OF_ROUNDS_COUNTER_TAG"
const val GENERATE_LOBBY_BUTTON_TAG = "GENERATE_LOBBY_BUTTON_TAG"
const val maxlines = 3
const val WEIGHT = 1f

@Composable
fun CreateLobbyView(
    modifier: Modifier = Modifier,
    lobbyData: CreateLobbyInputModel,
    viewModel: LobbiesViewModel,
    onNavigate: (LobbiesNavigationIntent) -> Unit = { },
) {
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.create_lobby_title_text),
                onBackIntent = { onNavigate(LobbiesNavigationIntent.Back) },
            )
        },
        modifier = Modifier.testTag(CREATE_LOBBY_SCAFFOLD_TAG)
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spaced16),
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Dimensions.padding16)
                    .verticalScroll(rememberScrollState()),
        ) {
            TextField(
                value = lobbyData.name,
                onValueChange = { viewModel.updateLobbyName(it) },
                label = { Text(stringResource(R.string.lobby_name_text)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag(LOBBY_NAME_TEXTFIELD_TAG),
            )

            TextField(
                value = lobbyData.description,
                onValueChange = { viewModel.updateLobbyDescription(it) },
                label = { Text(stringResource(R.string.description_text)) },
                modifier = Modifier.fillMaxWidth().testTag(LOBBY_DESCRIPTION_TEXTFIELD_TAG),
                maxLines = maxlines,
            )
            TextField(
                value = if (lobbyData.costToPlay == 0) "" else lobbyData.costToPlay.toString(),
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        val newValue = input.toIntOrNull() ?: 0
                        viewModel.updateCostToPlay(newValue)
                    }
                },
                label = { Text(stringResource(R.string.cost_to_play_text)) },
                isError = lobbyData.costToPlay <= 0,
                supportingText = {
                    if (lobbyData.costToPlay <= 0) {
                        Text(stringResource(R.string.value_above_zero_text))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag(PRICE_TEXTFIELD_TAG),
            )
            CounterRow(
                label = stringResource(R.string.min_players_text),
                value = lobbyData.minPlayers,
                onDec = { viewModel.decMinPlayers() },
                onInc = { viewModel.incMinPlayers() },
                decEnabled = lobbyData.minPlayers > MIN_PLAYERS,
                incEnabled = lobbyData.minPlayers < lobbyData.maxPlayers,
                testTag = MIN_PLAYERS_COUNTER_TAG
            )
            CounterRow(
                label = stringResource(R.string.max_players_text),
                value = lobbyData.maxPlayers,
                onDec = { viewModel.decMaxPlayers() },
                onInc = { viewModel.incMaxPlayers() },
                decEnabled = lobbyData.maxPlayers > lobbyData.minPlayers,
                incEnabled = lobbyData.maxPlayers < MAX_PLAYERS,
                testTag = MAX_PLAYERS_COUNTER_TAG
            )
            CounterRow(
                label = stringResource(R.string.num_rounds_text),
                value = lobbyData.numberOfRounds,
                onDec = { viewModel.decRounds() },
                onInc = { viewModel.incRounds() },
                decEnabled = lobbyData.numberOfRounds > lobbyData.minPlayers,
                incEnabled = lobbyData.numberOfRounds < MAX_ROUNDS,
                testTag = NUMBER_OF_ROUNDS_COUNTER_TAG
            )

            Spacer(modifier = Modifier.weight(WEIGHT))

            val isRoundsValid =
                lobbyData.numberOfRounds >= lobbyData.minPlayers &&
                    lobbyData.numberOfRounds <= (lobbyData.maxPlayers * 2)

            Button(
                onClick = { viewModel.createLobby(lobbyData) },
                enabled =
                    lobbyData.name.isNotBlank() &&
                        lobbyData.description.isNotBlank() &&
                        lobbyData.costToPlay > 0 &&
                        isRoundsValid,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimensions.height50)
                        .testTag(GENERATE_LOBBY_BUTTON_TAG),
            ) {
                Text(stringResource(R.string.create_lobby_text))
            }

            if (!isRoundsValid) {
                Text(
                    text = stringResource(R.string.rounds_between_text) + " ${lobbyData.minPlayers}" + stringResource(R.string.and_text) + "${lobbyData.maxPlayers * 2}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = Dimensions.padding4),
                )
            }
        }
    }
}

