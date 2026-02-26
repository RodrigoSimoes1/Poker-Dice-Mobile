package com.example.myapplication.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.R
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.ui.theme.Dimensions

const val START_BUTTON_TAG = "start_button"

enum class MainScreenNavigationIntent {
    NavigateToAbout,
    NavigateToProfile,
    NavigateToLobbys,
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigate: (MainScreenNavigationIntent) -> Unit = { },
) {
    Scaffold(
        topBar = {
            TopBar(
                onInfoIntent = { onNavigate(MainScreenNavigationIntent.NavigateToAbout) },
                onProfileIntent = { onNavigate(MainScreenNavigationIntent.NavigateToProfile) },
            )
        },
        content = { contentPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing),
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(Dimensions.screenPadding),
            ) {
                Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

                Text(
                    text = "Poker Dice",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )

                // Imagem do dado
                Image(
                    painter = painterResource(id = R.drawable.dado),
                    contentDescription = "Dado",
                    modifier = Modifier.size(Dimensions.iconSizeLarge),
                )

                // Botão de iniciar jogo
                Button(
                    onClick = { onNavigate(MainScreenNavigationIntent.NavigateToLobbys) },
                    modifier = Modifier.testTag(START_BUTTON_TAG),
                ) {
                    Text(
                        text = stringResource(id = R.string.start_game_button_text),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TitleScreenPreview() {
    MainScreen()
}
