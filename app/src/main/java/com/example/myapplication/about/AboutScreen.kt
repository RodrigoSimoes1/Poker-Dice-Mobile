package com.example.myapplication.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.R
import com.example.myapplication.commonElements.Title
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.ui.theme.Dimensions

const val AUTHOR_SECTION_TAG = "author_section"
const val RULES_SECTION_TAG = "rules_section"
const val RULES_URL = "https://www.coololdgames.com/dice-games/gambling/poker-dice/#how-to-play-poker-dice"
const val AUTHORS = "Rodrigo Simões - 51405\nAndré Galvão - 51510"

sealed class AboutScreenNavigationIntent {
    object NavigateBack : AboutScreenNavigationIntent()

    class Email(
        val destination: Array<String>,
        val subject: String,
    ) : AboutScreenNavigationIntent()

    class Browser(
        val destination: String,
    ) : AboutScreenNavigationIntent()
}

val EMAILS = arrayOf("galvatronbgpt@gmail.com", "rsimoes595@gmail.com")
const val SUBJECT = "About the Poker dice android application"

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onNavigate: (AboutScreenNavigationIntent) -> Unit = { },
) {
    Scaffold(
        topBar = {
            TopBar(
                onBackIntent = { onNavigate(AboutScreenNavigationIntent.NavigateBack) },
            )
        },
        content = { contentPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacingVertical),
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(Dimensions.screenPadding),
            ) {
                Title(R.string.about_button_text)

                Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

                AuthorSection(
                    onSendEmailRequested = {
                        onNavigate(
                            AboutScreenNavigationIntent.Email(
                                destination = EMAILS,
                                subject = SUBJECT,
                            ),
                        )
                    },
                )

                Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

                RulesSection(
                    onRulesRequested = {
                        onNavigate(
                            AboutScreenNavigationIntent.Browser(
                                destination = RULES_URL,
                            ),
                        )
                    },
                )
            }
        },
    )
}

@Composable
private fun AuthorSection(onSendEmailRequested: () -> Unit = { }) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.rowSpacing),
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onSendEmailRequested() }
                .testTag(AUTHOR_SECTION_TAG),
    ) {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = "Send Email",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimensions.iconSize),
        )
        Text(
            text = stringResource(id = R.string.email_button_text),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = AUTHORS,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun RulesSection(onRulesRequested: () -> Unit = { }) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.rowSpacing),
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onRulesRequested() }
                .testTag(RULES_SECTION_TAG),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = "Game Rules",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(Dimensions.iconSize),
        )
        Text(
            text = stringResource(id = R.string.rules_button_text),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(id = R.string.how_to_play_text),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}
