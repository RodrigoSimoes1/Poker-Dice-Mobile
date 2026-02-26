package com.example.myapplication.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.commonElements.Title
import com.example.myapplication.ui.theme.Dimensions

const val PROFILE_MAIN_COLUMN_TAG = "profile_main_column"
const val PROFILE_TITLE_TEST_TAG = "title_test_tag"
const val PROFILE_NAME_TAG = "profile_name"
const val PROFILE_GAMES_WON_TAG = "profile_hands"
const val PROFILE_GAMES_PLAYED_TAG = "profile_games_played"
const val PROFILE_MONEY_TAG = "profile_money"
const val SHOP_CARD_TAG = "profile_shop_card"

const val NO_INVITE_CODE_BUTTON_TAG = "no_invite_code_button"
const val INVITE_CODE_INFO_TAG = "invite_code_info"
const val LOGOUT_BUTTON_TEST_TAG = "logout_button"
const val GAMES_PLAYED_NUM_TAG = "games_played_num"
const val GAMES_PLAYED_TEXT_TAG = "games_played_text"
const val GAMES_WON_NUM_TAG = "games_won_num"
const val GAMES_WON_TEXT_TAG = "games_won"
const val PROFILE_CONTROLER_ICON_TAG = "profile_controller_icon"
const val PROFILE_TROPHY_ICON_TAG = "profile_trophy_icon"
const val INVITE_CODE_ICON_TAG = "invite_code_icon"
const val GENERATE_INVITE_CODE_TEXT_TAG = "generate_invite_code_text"
const val SHOW_INVITE_CODE_TEXT_TAG = "show_invite_code_text"
const val LOGOUT_BUTTON_TEXT_TAG = "logout_button_text"
const val INVITE_CODE_TAG = "invite_code"
const val FRACT = 0.8f





enum class ProfileScreenNavigationIntent {
    NavigateToHome,
    NavigateToAbout,
    NavigateToLogin,
    NavigateToShop,
}

@Composable
fun ProfileView(
    modifier: Modifier = Modifier,
    onNavigate: (ProfileScreenNavigationIntent) -> Unit = { },
    name: String,
    gamesPlayed: Int,
    gamesWon: Int,
    balance: Int,
    inviteCode: String?,
    onGenerateInvite: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing),
        modifier =
            modifier
                .fillMaxSize()
                .padding(Dimensions.screenPadding)
                .verticalScroll(rememberScrollState())
                .testTag(PROFILE_MAIN_COLUMN_TAG),
    ) {
        //Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

        Title(id = R.string.profile_button_text, testTag = PROFILE_TITLE_TEST_TAG)

        //Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

        ProfileInfo(
            name = name,
            gamesPlayed = gamesPlayed,
            gamesWon = gamesWon,
            balance = balance,
            onNavigateToShop = { onNavigate(ProfileScreenNavigationIntent.NavigateToShop) },
        )

        //Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

        if (inviteCode == null) {
            Button(
                onClick = onGenerateInvite,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    ),
                modifier = Modifier.testTag(NO_INVITE_CODE_BUTTON_TAG),
            ) {
                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.padding(end = Dimensions.padding8).testTag(INVITE_CODE_ICON_TAG))
                Text(stringResource(id = R.string.generate_invite_code_text), modifier = Modifier.testTag(GENERATE_INVITE_CODE_TEXT_TAG))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(Dimensions.roundedCornerShape12),
                        ).padding(Dimensions.padding16)
                        .fillMaxWidth(FRACT)
                        .testTag(INVITE_CODE_INFO_TAG),
            ) {
                Text(
                    text = stringResource(id = R.string.your_code_text),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag(SHOW_INVITE_CODE_TEXT_TAG)
                )
                Text(
                    text = inviteCode,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = Dimensions.letterSpacing,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = Dimensions.padding8).testTag(INVITE_CODE_TAG),
                )
            }
        }

        //Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))

        Button(
            onClick = { onNavigate(ProfileScreenNavigationIntent.NavigateToLogin) },
            modifier = Modifier.fillMaxWidth().testTag(LOGOUT_BUTTON_TEST_TAG),
        ) {
            Text(stringResource(id = R.string.logout_button_text), modifier = Modifier.testTag(LOGOUT_BUTTON_TEXT_TAG))
        }
    }
}

@Composable
private fun ProfileInfo(
    name: String,
    gamesPlayed: Int,
    gamesWon: Int,
    balance: Int,
    onNavigateToShop: () -> Unit,
    testTag: String = "default case",
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing),
        modifier = Modifier.fillMaxWidth().testTag(testTag),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag(PROFILE_NAME_TAG),
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            StatItem(
                icon = Icons.Default.SportsEsports,
                label = stringResource(id = R.string.text_for_games_played),
                value = gamesPlayed.toString(),
                testTag = PROFILE_GAMES_PLAYED_TAG,
                iconTestTag = PROFILE_CONTROLER_ICON_TAG,
                numTag = GAMES_PLAYED_NUM_TAG,
                textTag = GAMES_PLAYED_TEXT_TAG,
            )
            StatItem(
                icon = Icons.Default.EmojiEvents,
                label = stringResource(id = R.string.text_for_games_won),
                value = gamesWon.toString(),
                testTag = PROFILE_GAMES_WON_TAG,
                iconTestTag = PROFILE_TROPHY_ICON_TAG,
                numTag = GAMES_WON_NUM_TAG,
                textTag = GAMES_WON_TEXT_TAG,
            )
        }

        //Spacer(modifier = Modifier.height(8.dp))

        Card(
            onClick = {
                onNavigateToShop()
            },
            modifier = Modifier.fillMaxWidth(FRACT).testTag(SHOP_CARD_TAG),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.padding16)
                        .testTag(PROFILE_MONEY_TAG),
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Balance",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimensions.size40),
                )
                //Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$$balance",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(id = R.string.text_for_balance),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                //Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.size16),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = stringResource(id = R.string.buy_more_text),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    testTag: String,
    iconTestTag: String,
    numTag: String,
    textTag: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.rowSpacing),
        modifier = Modifier.testTag(testTag),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.secondary,
            modifier =
                Modifier
                    .size(Dimensions.iconSize)
                    .testTag(iconTestTag),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag(numTag),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.testTag(textTag),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileView(
        name = "Rodrigo",
        gamesPlayed = 100,
        gamesWon = 70,
        balance = 15,
        inviteCode = "ABC123",
    )
}
