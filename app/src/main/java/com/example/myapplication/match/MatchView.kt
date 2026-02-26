package com.example.myapplication.match

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.domainlogic.calculateTotalScore
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.PlayerOutputModel
import com.example.myapplication.ui.theme.Dimensions
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

const val MATCH_MAIN_BOX = "MATCH_MAIN_BOX"
const val MATCH_TABLE_BASE = "MATCH_TABLE_BASE"
const val MATCH_DICE_AREA_PLAYING = "MATCH_DICE_AREA_PLAYING"
const val MATCH_DICE_AREA_OUT_OF_PLAY = "MATCH_DICE_AREA_OUT_OF_PLAY"
const val MATCH_PLAYERS_TAG = "MATCH_PLAYERS_TAG"
const val REROLL_HAND_TAG = "REROLL_HAND_TAG"
const val ACCEPT_HAND_BUTTON_TAG = "ACCEPT_HAND_BUTTON_TAG"
const val MAX_LIN = 1
const val TRIES_LEFT = 3
const val FACE_DELAY = 80L
const val ROLL_DURATION = 500L
const val ALPHA1 = 0.7f
const val ALPHA2 = 0.1f
const val ALPHA3 = 0.5f
const val ALPHA4 = 1f
const val ALPHA5 = 1.5f
const val ANGLE = 360f
const val QUARTER = 90
const val OUR_YELLOW = 0xFFFFD700
const val OUR_GRAY = 0xFFBDBDBD
const val OUR_DARK_GRAY = 0xFF424242
const val OUR_LIGHT_GREEN = 0xFF81C784
const val OUR_GREEN = 0xFF43A047
const val OUR_DARK_GREEN = 0xFF2E7D32
const val OUR_DARK_RED = 0xFF3E2723
const val OUR_DARK_BLUE = 0xFF1A237E




@Composable
fun MatchView(
    modifier: Modifier = Modifier,
    state: MatchState.Playing,
    onRoll: () -> Unit,
    onAccept: () -> Unit,
    onToggleDice: (Int) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().testTag(MATCH_MAIN_BOX)) {
        if (state.matchInfo != null) {
            PlayersAroundTheTable(
                players = state.matchInfo.players,
                currentTurnUserId = state.currentTurn?.userId,
                modifier = Modifier.size(Dimensions.size300).align(Alignment.Center).testTag(MATCH_PLAYERS_TAG),
            )
        }

        MatchTableBase(
            modifier = Modifier.size(Dimensions.size250).align(Alignment.Center).testTag(MATCH_TABLE_BASE),
        )

        if (state.currentTurn != null) {
            DiceArea(
                currentTurn = state.currentTurn,
                isMyTurn = state.isMyTurn,
                dicesToKeep = state.dicesToKeep,
                isRolling = state.isRolling,
                onRoll = onRoll,
                onAccept = onAccept,
                onToggleDice = onToggleDice,
                modifier = Modifier.wrapContentSize().align(Alignment.Center).testTag(MATCH_DICE_AREA_PLAYING),
            )
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center).testTag(MATCH_DICE_AREA_OUT_OF_PLAY),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(Dimensions.height8))
                Text(
                    text = stringResource(id = R.string.syncronizing_the_table_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun PlayersAroundTheTable(
    players: List<PlayerOutputModel>,
    currentTurnUserId: Int?,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (players.isEmpty()) return@BoxWithConstraints

        val radius = min(maxWidth, maxHeight) / 2f
        val angleStep = ANGLE / players.size

        players.forEachIndexed { index, player ->
            val angleRad = Math.toRadians((angleStep * index - QUARTER).toDouble())

            val x = cos(angleRad) * radius.value
            val y = sin(angleRad) * radius.value

            PlayerSeat(
                player = player,
                isMe = player.name.contains("Tu") || player.name.contains("Hero"),
                isActiveTurn = player.id == currentTurnUserId,
                modifier = Modifier.offset(x.dp, y.dp),
            )
        }
    }
}

@Composable
fun PlayerSeat(
    player: PlayerOutputModel,
    isMe: Boolean,
    isActiveTurn: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .size(if (isActiveTurn) Dimensions.size60 else Dimensions.size50)
                    .background(
                        color = if (isMe) Color(OUR_YELLOW) else Color(OUR_DARK_GRAY),
                        shape = CircleShape,
                    ).border(
                        width = Dimensions.width2,
                        color = if (isMe) Color.White else Color(OUR_GRAY),
                        shape = CircleShape,
                    ),
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isMe) Color.Black else Color.White,
                modifier = Modifier.size(Dimensions.size30),
            )
        }

        if (isActiveTurn) {
            Text(stringResource(id = R.string.playing_text), style = MaterialTheme.typography.labelSmall, color = Color(OUR_YELLOW))
        }

        Spacer(modifier = Modifier.size(Dimensions.size4))

        Surface(
            color = Color.Black.copy(alpha = ALPHA1),
            shape = RoundedCornerShape(Dimensions.roundedCornerShape8),
            border = androidx.compose.foundation.BorderStroke(Dimensions.borderstroke1, Color(0xFF5D4037)),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = Dimensions.padding8, vertical = Dimensions.padding4),
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = MAX_LIN,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "${player.balance}€",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(OUR_LIGHT_GREEN),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun DiceArea(
    currentTurn: CurrentTurnOutputModel,
    isMyTurn: Boolean,
    dicesToKeep: List<Boolean>,
    isRolling: Boolean,
    onRoll: () -> Unit,
    onAccept: () -> Unit,
    onToggleDice: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spaced16),
        modifier = modifier,
    ) {
        val hasRolledAtLeastOnce = currentTurn.triesLeft < TRIES_LEFT

        if (isMyTurn) {
            if (hasRolledAtLeastOnce) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.spaced8)) {
                    currentTurn.dices.forEachIndexed { index, dice ->
                        val isKept = dicesToKeep.getOrElse(index) { true }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            DiceRollAnimation(
                                isRolling = isRolling && !isKept,
                                finalImageRes = mapDiceFaceToResource(dice.face.name),
                            )
                            if (currentTurn.triesLeft > 0) {
                                Checkbox(
                                    checked = isKept,
                                    onCheckedChange = { onToggleDice(index) },
                                    enabled = !isRolling,
                                )
                            }
                        }
                    }
                }
                Text(
                    text = currentTurn.handRank.name.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(OUR_YELLOW),
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Text(stringResource(id = R.string.your_turn_roll_dice_text), color = Color.White)
            }
        } else {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(Dimensions.roundedCorners16),
                border = androidx.compose.foundation.BorderStroke(Dimensions.width1, Color.White.copy(alpha = 0.3f)),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(Dimensions.padding24),
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(Dimensions.size32),
                    )
                    Spacer(modifier = Modifier.height(Dimensions.height16))
                    Text(
                        text = stringResource(id = R.string.turn_of_text) + " ${currentTurn.userName}...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.height8))

        if (isMyTurn) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.spaced16)) {
                if (currentTurn.triesLeft > 0) {
                    Button(onClick = onRoll, enabled = !isRolling, modifier = Modifier.testTag(REROLL_HAND_TAG)) {
                        val texto = if (currentTurn.triesLeft == TRIES_LEFT) stringResource(id = R.string.roll_the_dice_text) else stringResource(id = R.string.text_to_roll_the_dice) + " (${currentTurn.triesLeft})"
                        Text(texto)
                    }
                }
                if (hasRolledAtLeastOnce && currentTurn.triesLeft > 0) {
                    Button(
                        onClick = onAccept,
                        enabled = !isRolling,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(OUR_GREEN)),
                        modifier = Modifier.testTag(ACCEPT_HAND_BUTTON_TAG),
                    ) {
                        Text(stringResource(id = R.string.accept_hand_text))
                    }
                }
            }
            if (currentTurn.triesLeft == 0) {
                Text(stringResource(id = R.string.ending_turn_text), color = Color.White.copy(alpha = ALPHA1))
            }
        }
    }
}

@Composable
fun DiceRollAnimation(
    modifier: Modifier = Modifier,
    isRolling: Boolean,
    finalImageRes: Int,
) {
    val diceFaces =
        listOf(
            R.drawable.ace_icon,
            R.drawable.king_icon,
            R.drawable.queen_icon,
            R.drawable.jack_icon,
            R.drawable.ten_icon,
            R.drawable.nine_icon,
        )

    var currentFace by remember { mutableIntStateOf(finalImageRes) }

    LaunchedEffect(isRolling, finalImageRes) {
        if (isRolling) {
            val rollDuration = ROLL_DURATION
            val faceDelay = FACE_DELAY
            val iterations = (rollDuration / faceDelay).toInt()

            repeat(iterations) {
                currentFace = diceFaces.random()
                delay(faceDelay)
            }
        }
        currentFace = finalImageRes
    }

    Image(
        painter = painterResource(id = currentFace),
        contentDescription = "Dice",
        modifier =
            modifier
                .size(Dimensions.size50)
                .background(Color.White, RoundedCornerShape(Dimensions.roundedCornerShape8))
                .border(Dimensions.width1, Color.Black, RoundedCornerShape(Dimensions.roundedCornerShape8))
                .padding(Dimensions.padding4),
    )
}

@Composable
fun MatchTableBase(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .background(Color(OUR_DARK_GREEN), CircleShape)
                .border(Dimensions.width8, Color(OUR_DARK_RED), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
    }
}

fun mapDiceFaceToResource(faceName: String): Int =
    when (faceName.uppercase()) {
        "ACE" -> R.drawable.ace_icon
        "KING" -> R.drawable.king_icon
        "QUEEN" -> R.drawable.queen_icon
        "JACK" -> R.drawable.jack_icon
        "TEN" -> R.drawable.ten_icon
        "NINE" -> R.drawable.nine_icon
        else -> R.drawable.ace_icon
    }

@Composable
fun RoundResultOverlay(
    players: List<PlayerOutputModel>,
    countdown: Int,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth(0.9f)
                    .padding(Dimensions.padding16),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
            elevation = CardDefaults.cardElevation(Dimensions.cardElevation),
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.padding16),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.end_of_round_text_text),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(Dimensions.height16))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(stringResource(id = R.string.text_for_player), color = Color.Gray, modifier = Modifier.weight(ALPHA4))
                    Text(stringResource(id = R.string.text_for_hand), color = Color.Gray, modifier = Modifier.weight(ALPHA5), textAlign = TextAlign.Center)
                    Text(stringResource(id = R.string.points_text), color = Color.Gray, modifier = Modifier.weight(ALPHA3), textAlign = TextAlign.End)
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Dimensions.padding8),
                    thickness = Dimensions.width1,
                    color = Color.Gray,
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spaced12),
                    modifier = Modifier.heightIn(max = Dimensions.size300),
                ) {
                    items(players) { player ->
                        RoundPlayerRow(player)
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.height24))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(Dimensions.width8))
                    Text(
                        text = stringResource(id = R.string.next_round_in_text) + " $countdown...",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                LinearProgressIndicator(
                    progress = { countdown / 5f }, // Assumindo max 5s
                    modifier = Modifier.fillMaxWidth().padding(top = Dimensions.padding8),
                )
            }
        }
    }
}

@Composable
fun RoundPlayerRow(player: PlayerOutputModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = player.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(ALPHA4),
            maxLines = MAX_LIN,
        )

        Row(
            modifier = Modifier.weight(ALPHA5),
            horizontalArrangement = Arrangement.Center,
        ) {
            player.playerHand?.dices?.forEach { dice ->
                Image(
                    painter = painterResource(id = mapDiceFaceToResource(dice.face.name)),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(Dimensions.size20)
                            .padding(Dimensions.padding1)
                            .background(Color.White, RoundedCornerShape(Dimensions.roundedCorners2)),
                )
            }
        }

        val points = calculateTotalScore(player.playerHand?.dices, player.playerHand?.handRank)

        Text(
            text = "$points pts",
            color = Color(OUR_YELLOW),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(ALPHA3),
        )
    }
}

@Composable
fun MatchFinishedView(
    testTag:String = "default",
    winner: PlayerOutputModel,
    countdown: Int,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(OUR_DARK_BLUE),
                )
                .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(Dimensions.padding32),
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Winner",
                tint = Color(OUR_YELLOW),
                modifier = Modifier.size(Dimensions.size120),
            )

            Spacer(modifier = Modifier.height(Dimensions.height24))

            Text(
                text = stringResource(id = R.string.winner_text),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = ALPHA1),
                letterSpacing = Dimensions.letterSpacing,
            )

            Spacer(modifier = Modifier.height(Dimensions.height8))

            Text(
                text = winner.name,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Dimensions.height16))

            Surface(
                color = Color.White.copy(alpha = ALPHA2),
                shape = RoundedCornerShape(Dimensions.roundedCorners16),
                border = androidx.compose.foundation.BorderStroke(Dimensions.width1, Color(OUR_YELLOW)),
            ) {
                Text(
                    text = "Total: ${winner.balance}€",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(OUR_YELLOW),
                    modifier = Modifier.padding(horizontal = Dimensions.padding24, vertical = Dimensions.padding12),
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.height48))

            Spacer(modifier = Modifier.height(Dimensions.height16))

            Text(
                text = stringResource(id = R.string.redirectioning_text) + " $countdown " + stringResource(id = R.string.seconds_with_dots_text),
                color = Color.White.copy(alpha = ALPHA3),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
