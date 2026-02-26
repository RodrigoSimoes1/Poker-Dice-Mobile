package com.example.myapplication.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domainlogic.Dice
import com.example.myapplication.domainlogic.DiceFace
import com.example.myapplication.domainlogic.Hand
import com.example.myapplication.domainlogic.HandRank
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.match.domain.MatchService
import com.example.myapplication.match.domain.MatchSseService
import com.example.myapplication.match.model.input.RollDiceInputModel
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.MatchEvent
import com.example.myapplication.match.model.output.MatchSsePayload
import com.example.myapplication.match.model.output.MatchStatusOutputModel
import com.example.myapplication.match.model.output.PlayerOutputModel
import com.example.myapplication.match.model.output.PlayerSsePayload
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MatchState {
    data object Loading : MatchState

    data class Error(
        val message: String,
    ) : MatchState

    data class Playing(
        val lobbyId: Int,
        val matchInfo: MatchStatusOutputModel?,
        val currentTurn: CurrentTurnOutputModel?,
        val isMyTurn: Boolean,
        val dicesToKeep: List<Boolean> = listOf(false, false, false, false, false),
        val isRolling: Boolean = false,
    ) : MatchState

    data class RoundEnded(
        val players: List<PlayerOutputModel>,
        val countdown: Int = 5,
    ) : MatchState

    data class MatchEnded(
        val winner: PlayerOutputModel,
        val finalRanking: List<PlayerOutputModel>,
        val countdown: Int = 10,
    ) : MatchState
}

class MatchViewModel(
    private val matchService: MatchService,
    private val authRepo: AuthInfoRepo,
    private val matchSseService: MatchSseService,
) : ViewModel() {
    companion object {
        fun getFactory(
            matchService: MatchService,
            authRepo: AuthInfoRepo,
            matchSseService: MatchSseService,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MatchViewModel(matchService, authRepo, matchSseService) as T
        }
    }

    private val _state = MutableStateFlow<MatchState>(MatchState.Loading)
    val state: StateFlow<MatchState> = _state.asStateFlow()

    private var currentLobbyId: Int = 0
    private var sseJob: Job? = null

    fun loadMatch(
        lobbyId: Int,
        hostId: Int,
    ) {
        if (currentLobbyId == lobbyId && _state.value !is MatchState.Error) return
        currentLobbyId = lobbyId

        viewModelScope.launch {
            val token = authRepo.getAuthInfo()?.authToken ?: return@launch
            val userId = authRepo.getAuthInfo()?.userId ?: return@launch

            matchService.getMatchStatus(token, lobbyId).fold(
                onSuccess = { match ->

                    val turnPlayerId = if (hostId != -1) hostId else match.players.first().id

                    val turnPlayer = match.players.find { it.id == turnPlayerId }

                    val initialTurn =
                        if (turnPlayer != null) {
                            CurrentTurnOutputModel(
                                userId = turnPlayer.id,
                                userName = turnPlayer.name,
                                pointsEarned = 0,
                                dices = List(5) { index -> Dice(index + 1, DiceFace.ACE) },
                                handRank = HandRank.BUST,
                                triesLeft = 3,
                            )
                        } else {
                            null
                        }

                    val isMyTurn = turnPlayerId == userId

                    _state.update {
                        MatchState.Playing(
                            lobbyId = currentLobbyId,
                            matchInfo = match,
                            currentTurn = initialTurn,
                            isMyTurn = isMyTurn,
                            dicesToKeep = listOf(false, false, false, false, false),
                            isRolling = false,
                        )
                    }

                    startMatchSse(match.id)
                },
                onFailure = { e ->
                    _state.value = MatchState.Error(e.message ?: "Erro ao carregar jogo")
                },
            )
        }
    }

    private fun startMatchSse(sseRoomId: Int) {
        sseJob?.cancel()
        sseJob =
            viewModelScope.launch {
                val myEmail = authRepo.getAuthInfo()?.userEmail ?: return@launch
                val token = authRepo.getAuthInfo()?.authToken ?: return@launch

                matchSseService.subscribeToMatch(sseRoomId).collect { event ->
                    if (_state.value is MatchState.MatchEnded && event !is MatchEvent.MatchWinner) return@collect

                    when (event) {
                        is MatchEvent.MatchUpdate -> {
                            val currentState = _state.value

                            if (currentState is MatchState.Playing) {
                                val oldTurnId = currentState.currentTurn?.userId ?: 0
                                val newTurnId = event.match.currentTurnPlayerId
                                if (oldTurnId != 0 && oldTurnId != newTurnId && currentState.isMyTurn) {
                                    delay(2000)
                                }
                            }

                            val newState = mapSseToPlayingState(event.match, myEmail)
                            _state.value = newState
                        }

                        is MatchEvent.RoundEnd -> {
                            matchService.getMatchStatus(token, currentLobbyId).fold(
                                onSuccess = { fullMatchStatus ->
                                    val players = fullMatchStatus.players
                                    for (i in 5 downTo 0) {
                                        _state.value = MatchState.RoundEnded(players, i)
                                        delay(1000)
                                    }
                                    val newState = mapSseToPlayingState(event.match, myEmail)
                                    _state.value = newState
                                },
                                onFailure = {
                                    val players = mapSsePlayersToOutput(event.match.players)
                                    for (i in 5 downTo 0) {
                                        _state.value = MatchState.RoundEnded(players, i)
                                        delay(1000)
                                    }
                                    val newState = mapSseToPlayingState(event.match, myEmail)
                                    _state.value = newState
                                },
                            )
                        }

                        is MatchEvent.MatchWinner -> {
                            val winner =
                                PlayerOutputModel(
                                    id = event.winnerInfo.winnerId,
                                    name = event.winnerInfo.winnerName,
                                    email = "",
                                    balance = event.winnerInfo.winnings,
                                    playerHand = null,
                                    gamesPlayed = 0,
                                    gamesWon = 0,
                                )

                            for (i in 10 downTo 0) {
                                _state.value = MatchState.MatchEnded(winner, emptyList(), i)
                                delay(1000)
                            }
                        }

                        is MatchEvent.Error -> {
                            println("Error: ${event.message}")
                        }
                    }
                }
            }
    }

    fun rollDice() {
        val currentState = _state.value as? MatchState.Playing ?: return
        if (!currentState.isMyTurn || currentState.currentTurn == null) return

        viewModelScope.launch {
            _state.update { currentState.copy(isRolling = true) }
            val token = authRepo.getAuthInfo()?.authToken ?: return@launch

            val input =
                RollDiceInputModel(
                    dice1 = if (currentState.dicesToKeep[0]) "KEEP" else "CHANGE",
                    dice2 = if (currentState.dicesToKeep[1]) "KEEP" else "CHANGE",
                    dice3 = if (currentState.dicesToKeep[2]) "KEEP" else "CHANGE",
                    dice4 = if (currentState.dicesToKeep[3]) "KEEP" else "CHANGE",
                    dice5 = if (currentState.dicesToKeep[4]) "KEEP" else "CHANGE",
                )

            matchService.rollDice(token, currentLobbyId, input).fold(
                onSuccess = { turn ->
                    if (_state.value is MatchState.MatchEnded) return@fold

                    if (turn.triesLeft > 0) {
                        _state.update {
                            currentState.copy(
                                isRolling = false,
                                currentTurn = turn,
                                dicesToKeep = listOf(false, false, false, false, false),
                                isMyTurn = true,
                            )
                        }
                    } else {
                        _state.update {
                            currentState.copy(
                                isRolling = false,
                                currentTurn = turn,
                                isMyTurn = true,
                            )
                        }
                    }
                },
                onFailure = {
                    _state.update { currentState.copy(isRolling = false) }
                },
            )
        }
    }

    fun acceptPlay() {
        val currentState = _state.value as? MatchState.Playing ?: return

        viewModelScope.launch {
            val token = authRepo.getAuthInfo()?.authToken ?: return@launch
            _state.update { currentState.copy(isRolling = true, isMyTurn = false) }

            matchService.acceptPlay(token, currentLobbyId).fold(
                onSuccess = {
                    if (_state.value is MatchState.MatchEnded) {
                        return@fold
                    }
                    _state.update { currentState.copy(isRolling = false, isMyTurn = false) }
                },
                onFailure = { e ->
                    _state.update { currentState.copy(isRolling = false) }
                },
            )
        }
    }

    fun toggleDiceSelection(index: Int) {
        val currentState = _state.value as? MatchState.Playing ?: return
        if (!currentState.isMyTurn || currentState.currentTurn == null) return

        val newList = currentState.dicesToKeep.toMutableList()
        newList[index] = !newList[index]
        _state.value = currentState.copy(dicesToKeep = newList)
    }

    private fun mapSseToPlayingState(
        sseMatch: MatchSsePayload,
        myEmail: String,
    ): MatchState.Playing {
        val uiPlayers = mapSsePlayersToOutput(sseMatch.players)
        val currentTurnPlayer = sseMatch.players.find { it.user.id == sseMatch.currentTurnPlayerId }

        val currentTurn =
            if (currentTurnPlayer != null) {
                CurrentTurnOutputModel(
                    userId = currentTurnPlayer.user.id,
                    userName = currentTurnPlayer.user.name,
                    pointsEarned = currentTurnPlayer.points,
                    dices = currentTurnPlayer.playerHand.dices.mapIndexed { index, d -> Dice(index + 1, safeDiceFace(d.face)) },
                    handRank = safeHandRank(currentTurnPlayer.playerHand.rank),
                    triesLeft = sseMatch.rollsLeft,
                )
            } else {
                null
            }

        val matchInfo =
            MatchStatusOutputModel(
                id = sseMatch.id,
                lobbyId = currentLobbyId,
                players = uiPlayers,
                isFinished = sseMatch.isFinished,
            )

        val myPlayerInfo =
            sseMatch.players.find {
                it.user.email
                    .trim()
                    .equals(myEmail.trim(), ignoreCase = true)
            }
        val myId = myPlayerInfo?.user?.id ?: -1
        val isItReallyMyTurn = (sseMatch.currentTurnPlayerId != 0) && (sseMatch.currentTurnPlayerId == myId)

        return MatchState.Playing(
            lobbyId = currentLobbyId,
            matchInfo = matchInfo,
            currentTurn = currentTurn,
            isMyTurn = isItReallyMyTurn,
            dicesToKeep = listOf(false, false, false, false, false),
            isRolling = false,
        )
    }

    private fun mapSsePlayersToOutput(ssePlayers: List<PlayerSsePayload>): List<PlayerOutputModel> =
        ssePlayers
            .sortedBy { it.user.id }
            .map { p ->
                PlayerOutputModel(
                    id = p.user.id,
                    name = p.user.name,
                    email = p.user.email,
                    balance = p.user.balance,
                    gamesPlayed = p.user.gamesPlayed,
                    gamesWon = p.user.gamesWon,
                    playerHand =
                        Hand(
                            dices =
                                p.playerHand.dices.mapIndexed { i, d ->
                                    Dice(i + 1, safeDiceFace(d.face))
                                },
                            handRank = safeHandRank(p.playerHand.rank),
                        ),
                )
            }

    private fun safeDiceFace(face: String): DiceFace =
        try {
            DiceFace.valueOf(face.uppercase())
        } catch (_: Exception) {
            DiceFace.ACE
        }

    private fun safeHandRank(rank: String): HandRank =
        try {
            HandRank.valueOf(rank.uppercase())
        } catch (_: Exception) {
            HandRank.BUST
        }
}
