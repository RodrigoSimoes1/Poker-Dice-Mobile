package com.example.myapplication.lobbies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.lobbies.domain.LobbyService
import com.example.myapplication.lobbies.domain.LobbySseService
import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import com.example.myapplication.lobbies.model.output.LobbyEvent
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.match.domain.MatchService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LobbiesScreenState {
    data object Idle : LobbiesScreenState()

    data class Loading(
        val message: String? = null,
    ) : LobbiesScreenState()

    data class LobbiesSelection(
        val lobbies: List<LobbyOutputModel>,
        val currentUserId: Int,
    ) : LobbiesScreenState()

    data class WaitingRoom(
        val lobby: LobbyOutputModel,
        val currentUserId: String,
    ) : LobbiesScreenState()

    data class CreatingLobby(
        val lobby: CreateLobbyInputModel,
    ) : LobbiesScreenState()

    data class Error(
        val errorMessage: String,
    ) : LobbiesScreenState()

    data class NavigateToMatch(
        val matchId: Int,
        val hostId: Int,
    ) : LobbiesScreenState()
}

open class LobbiesViewModel(
    private val lobbyService: LobbyService,
    private val authRepo: AuthInfoRepo,
    private val matchService: MatchService,
    private val lobbySseService: LobbySseService,
) : ViewModel() {
    companion object {
        const val MIN_PLAYERS = 2
        const val MAX_PLAYERS = 6
        const val MIN_ROUNDS = 2
        const val MAX_ROUNDS = 60

        fun getFactory(
            lobbyService: LobbyService,
            authRepo: AuthInfoRepo,
            matchService: MatchService,
            lobbySseService: LobbySseService,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    LobbiesViewModel(lobbyService, authRepo, matchService, lobbySseService) as T
            }
    }

    private var sseJob: Job? = null
    private val _currentState = MutableStateFlow<LobbiesScreenState>(LobbiesScreenState.Idle)
    open val currentState: StateFlow<LobbiesScreenState> = _currentState.asStateFlow()

    private var currentAuthInfo: AuthInfo? = null

    init {
        loadLobbies()
    }

    open fun startCreatingLobby() {
        _currentState.value =
            LobbiesScreenState.CreatingLobby(
                lobby =
                    CreateLobbyInputModel(
                        name = "",
                        description = "",
                        maxPlayers = 6,
                        minPlayers = 2,
                        numberOfRounds = 2,
                        costToPlay = 0,
                    ),
            )
    }

    private fun startGlobalSse() {
        sseJob?.cancel()

        sseJob =
            viewModelScope.launch {
                val token = authRepo.getAuthInfo()?.authToken ?: return@launch

                lobbySseService.subscribeGlobalLobbies(token).collect { event ->
                    val currentState = _currentState.value

                    if (currentState is LobbiesScreenState.LobbiesSelection) {
                        val currentList = currentState.lobbies
                        when (event) {
                            is LobbyEvent.AddOrUpdate -> {
                                val updatedList = currentList.filter { it.id != event.lobby.id } + event.lobby
                                _currentState.value = currentState.copy(lobbies = updatedList)
                            }
                            is LobbyEvent.Remove -> {
                                val updatedList = currentList.filter { it.id != event.lobbyId }
                                _currentState.value = currentState.copy(lobbies = updatedList)
                            }

                            else -> {}
                        }
                    }
                }
            }
    }

    private fun startLobbySse(lobbyId: Int) {
        sseJob?.cancel()

        sseJob =
            viewModelScope.launch {
                val token = authRepo.getAuthInfo()?.authToken ?: return@launch

                lobbySseService.subscribeLobbyState(token, lobbyId).collect { event ->
                    val currentState = _currentState.value

                    if (currentState is LobbiesScreenState.WaitingRoom && currentState.lobby.id == lobbyId) {
                        val currentLobby = currentState.lobby

                        when (event) {
                            is LobbyEvent.PlayerJoined -> {
                                val newUser =
                                    UserInLobbyOutputModel(
                                        id = event.userId,
                                        name = event.name,
                                        email = event.email,
                                    )

                                if (currentLobby.players.none { it.id == event.userId }) {
                                    val updatedPlayers = currentLobby.players + newUser
                                    val updatedLobby = currentLobby.copy(players = updatedPlayers)
                                    _currentState.value = currentState.copy(lobby = updatedLobby)
                                }
                            }

                            is LobbyEvent.PlayerLeft -> {
                                val updatedPlayers = currentLobby.players.filter { it.id != event.userId }
                                val updatedLobby = currentLobby.copy(players = updatedPlayers)
                                _currentState.value = currentState.copy(lobby = updatedLobby)
                            }

                            is LobbyEvent.LeaderLeft -> {
                                loadLobbies("O lobby foi encerrado pelo Host.")
                                delay(2000)
                            }

                            is LobbyEvent.AddOrUpdate -> {
                                _currentState.value = currentState.copy(lobby = event.lobby)
                            }

                            is LobbyEvent.MatchStarted -> {
                                val currentLobbyId = currentState.lobby.id
                                val hostId = currentState.lobby.host.id
                                _currentState.value =
                                    LobbiesScreenState.NavigateToMatch(
                                        matchId = currentLobbyId,
                                        hostId = hostId,
                                    )
                            }

                            else -> {}
                        }
                    }
                }
            }
    }

    fun startRealtimeUpdates() {
        val state = _currentState.value

        if (sseJob?.isActive == true) return

        when (state) {
            is LobbiesScreenState.WaitingRoom -> {
                startLobbySse(state.lobby.id)
            }
            else -> {
                startGlobalSse()
            }
        }
    }

    open fun loadLobbies(loadingMessage: String? = null) {
        startGlobalSse()

        viewModelScope.launch {
            _currentState.value = LobbiesScreenState.Loading(loadingMessage)

            currentAuthInfo = authRepo.getAuthInfo()
            val token = currentAuthInfo?.authToken ?: return@launch
            val userId = currentAuthInfo?.userId ?: return@launch
            val publicLobbiesResult = lobbyService.getAllAvailableLobbies(token)
            val myLobbyResult = lobbyService.getLobbyByUserId(token, userId)

            publicLobbiesResult.fold(
                onSuccess = { publicList ->
                    var finalList = publicList
                    myLobbyResult.onSuccess { myLobby ->

                        if (finalList.none { it.id == myLobby.id }) {
                            finalList = listOf(myLobby) + finalList
                        }
                    }
                    _currentState.value =
                        LobbiesScreenState.LobbiesSelection(
                            lobbies = finalList,
                            currentUserId = userId,
                        )
                },
                onFailure = { error ->
                    _currentState.value = LobbiesScreenState.Error(error.message ?: "Erro ao carregar lobbies")
                },
            )
        }
    }

    open fun createLobby(lobbyData: CreateLobbyInputModel) {
        viewModelScope.launch {
            _currentState.value = LobbiesScreenState.Loading()
            val token = currentAuthInfo!!.authToken

            lobbyService.createLobby(token, lobbyData).fold(
                onSuccess = { lobby ->
                    _currentState.value = LobbiesScreenState.WaitingRoom(lobby, currentAuthInfo!!.userEmail)
                    startLobbySse(lobby.id)
                },
                onFailure = { error ->
                    _currentState.value = LobbiesScreenState.Error(error.message ?: "Erro")
                },
            )
        }
    }

    open fun joinLobby(lobbyId: Int) {
        viewModelScope.launch {
            _currentState.value = LobbiesScreenState.Loading()
            val token = currentAuthInfo!!.authToken

            lobbyService.joinLobby(token, lobbyId).fold(
                onSuccess = { lobby ->
                    _currentState.value = LobbiesScreenState.WaitingRoom(lobby, currentAuthInfo!!.userEmail)
                    startLobbySse(lobbyId)
                },
                onFailure = { error ->
                    _currentState.value = LobbiesScreenState.Error(error.message ?: "Erro")
                },
            )
        }
    }

    open fun leaveLobby(lobbyId: Int) {
        viewModelScope.launch {
            val token = currentAuthInfo!!.authToken

            lobbyService.leaveLobby(token, lobbyId).fold(
                onSuccess = {
                    loadLobbies()
                },
                onFailure = {
                    loadLobbies()
                },
            )
        }
    }

    fun startMatch(
        lobbyId: Int,
        onNavigateToMatch: () -> Unit,
    ) {
        viewModelScope.launch {
            _currentState.value = LobbiesScreenState.Loading()
            val token = currentAuthInfo?.authToken ?: return@launch
            val myId = currentAuthInfo?.userId ?: return@launch

            matchService.startMatch(token, lobbyId).fold(
                onSuccess = { matchOutput ->
                    _currentState.value =
                        LobbiesScreenState.NavigateToMatch(
                            matchId = lobbyId,
                            hostId = myId,
                        )
                    onNavigateToMatch()
                },
                onFailure = { error ->
                    _currentState.value = LobbiesScreenState.Error(error.message ?: "Erro ao iniciar partida")
                },
            )
        }
    }

    fun returnToLobby(lobby: LobbyOutputModel) {
        val email = currentAuthInfo?.userEmail ?: return
        _currentState.value = LobbiesScreenState.WaitingRoom(lobby, email)
        startLobbySse(lobby.id)
    }

    fun updateLobbyName(name: String) {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(name = name),
                )
        }
    }

    fun updateLobbyDescription(description: String) {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(description = description),
                )
        }
    }

    fun updateCostToPlay(cost: Int) {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(costToPlay = cost),
                )
        }
    }

    fun incMinPlayers() {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            val newValue = (currentState.lobby.minPlayers + 1).coerceAtMost(currentState.lobby.maxPlayers)
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(minPlayers = newValue),
                )
        }
    }

    fun decMinPlayers() {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            val newValue = (currentState.lobby.minPlayers - 1).coerceAtLeast(MIN_PLAYERS)
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(minPlayers = newValue),
                )
        }
    }

    fun incMaxPlayers() {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            val newValue = (currentState.lobby.maxPlayers + 1).coerceAtMost(MAX_PLAYERS)
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(maxPlayers = newValue),
                )
        }
    }

    fun decMaxPlayers() {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            val newValue = (currentState.lobby.maxPlayers - 1).coerceAtLeast(currentState.lobby.minPlayers)
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(maxPlayers = newValue),
                )
        }
    }

    fun incRounds() {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            val newValue = (currentState.lobby.numberOfRounds + 1).coerceAtMost(MAX_ROUNDS)
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(numberOfRounds = newValue),
                )
        }
    }

    fun decRounds() {
        val currentState = _currentState.value
        if (currentState is LobbiesScreenState.CreatingLobby) {
            val newValue = (currentState.lobby.numberOfRounds - 1).coerceAtLeast(MIN_ROUNDS)
            _currentState.value =
                currentState.copy(
                    lobby = currentState.lobby.copy(numberOfRounds = newValue),
                )
        }
    }

    open fun onCancelCreation() {
        loadLobbies()
    }
}
