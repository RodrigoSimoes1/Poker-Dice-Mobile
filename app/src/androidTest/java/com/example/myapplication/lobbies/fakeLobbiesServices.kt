package com.example.myapplication.lobbies

import com.example.myapplication.lobbies.domain.LobbyService
import com.example.myapplication.lobbies.domain.LobbySseService
import com.example.myapplication.lobbies.model.input.CreateLobbyInputModel
import com.example.myapplication.lobbies.model.output.LobbyEvent
import com.example.myapplication.lobbies.model.output.LobbyOutputModel
import com.example.myapplication.lobbies.model.output.UserInLobbyOutputModel
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.match.domain.MatchService
import com.example.myapplication.match.model.input.RollDiceInputModel
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.MatchOutputModel
import com.example.myapplication.match.model.output.MatchStatusOutputModel
import com.example.myapplication.profile.model.UserOutputModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class FakeAuthInfoRepo(
    private var storedAuth: AuthInfo? = null
) : AuthInfoRepo {

    override val authInfo: Flow<AuthInfo?> = flow {
        emit(storedAuth)
    }

    override suspend fun getAuthInfo(): AuthInfo? = storedAuth

    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
        storedAuth = authInfo
    }

    override suspend fun clearAuthInfo() {
        storedAuth = null
    }
}


abstract class FakeLobbyService(
    private val lobbies: MutableList<LobbyOutputModel> = mutableListOf(),
    private val myLobby: LobbyOutputModel? = null
) : LobbyService {

    override suspend fun getAllAvailableLobbies(token: String): Result<List<LobbyOutputModel>> =
        Result.success(lobbies.toList())

    override suspend fun getLobbyByUserId(token: String, userId: Int): Result<LobbyOutputModel> =
        myLobby?.let { Result.success(it) }
            ?: Result.failure(Exception("User has no lobby"))

    override suspend fun getLobbyById(
        token: String,
        lobbyId: Int
    ): Result<LobbyOutputModel> =
        lobbies.firstOrNull { it.id == lobbyId }
            ?.let { Result.success(it) }
            ?: Result.failure(Exception("Lobby not found"))

    override suspend fun createLobby(
        token: String,
        input: CreateLobbyInputModel
    ): Result<LobbyOutputModel> {
        val lobby =
            LobbyOutputModel(
                id = lobbies.size + 1,
                name = input.name,
                description = input.description,
                players = listOf(UserInLobbyOutputModel(0, "John Test", "test@email.com")),
                minPlayers = input.minPlayers,
                maxPlayers = input.maxPlayers,
                costToPlay = input.costToPlay,
                numberOfRounds = input.numberOfRounds,
                isPublic = true,
                host = UserInLobbyOutputModel(0, "John Test", "test@email.com")
            )

        lobbies.add(lobby)
        return Result.success(lobby)
    }

    override suspend fun joinLobby(token: String, lobbyId: Int): Result<LobbyOutputModel> =
        lobbies.firstOrNull { it.id == lobbyId }
            ?.let { Result.success(it) }
            ?: Result.failure(Exception("Lobby not found"))

    override suspend fun leaveLobby(token: String, lobbyId: Int): Result<LobbyOutputModel> =
        Result.success(lobbies.first { it.id == lobbyId })
}



class FakeLobbySseService : LobbySseService {

    private val globalEvents = MutableSharedFlow<LobbyEvent>()
    private val lobbyEvents = MutableSharedFlow<LobbyEvent>()

    override suspend fun subscribeGlobalLobbies(token: String): Flow<LobbyEvent> =
        globalEvents

    override suspend fun subscribeLobbyState(token: String, lobbyId: Int): Flow<LobbyEvent> =
        lobbyEvents

    suspend fun emitGlobal(event: LobbyEvent) {
        globalEvents.emit(event)
    }

    suspend fun emitLobby(event: LobbyEvent) {
        lobbyEvents.emit(event)
    }
}


class FakeMatchService(
    private val shouldSucceed: Boolean = true
) : MatchService {

    override suspend fun startMatch(token: String, lobbyId: Int): Result<MatchOutputModel> =
        if (shouldSucceed) {
            Result.success(
                MatchOutputModel(
                    id = 0,
                    lobbyId = lobbyId,
                    isFinished = false,
                    players =
                        listOf(
                            UserOutputModel(0, "John Test", "test@email.com", 100, 10, 10),
                            UserOutputModel(1, "John Test2", "test2@email.com", 100, 10, 10)
                        )
                )
            )
        } else {
            Result.failure(Exception("Match failed"))
        }

    override suspend fun getMatchStatus(
        token: String,
        lobbyId: Int
    ): Result<MatchStatusOutputModel> =
        Result.failure(Exception("Not needed"))

    override suspend fun rollDice(
        token: String,
        lobbyId: Int,
        input: RollDiceInputModel
    ): Result<CurrentTurnOutputModel> =
        Result.failure(Exception("Not needed"))

    override suspend fun acceptPlay(
        token: String,
        lobbyId: Int
    ): Result<MatchOutputModel> =
        Result.failure(Exception("Not needed"))

    class SpyLobbiesViewModel(
        lobbyService: FakeLobbyService,
        authRepo: FakeAuthInfoRepo,
        matchService: FakeMatchService,
        lobbySseService: FakeLobbySseService,
    ) : LobbiesViewModel(
        lobbyService,
        authRepo,
        matchService,
        lobbySseService,
    ) {
        var createLobbyCalled = false
        var receivedInput: CreateLobbyInputModel? = null

        override fun createLobby(input: CreateLobbyInputModel) {
            createLobbyCalled = true
            receivedInput = input
        }
    }

    class SpyLobbiesViewModelSelection(
        initialState: LobbiesScreenState
    ) : LobbiesViewModel(
        lobbyService = object : FakeLobbyService() {},
        authRepo = FakeAuthInfoRepo(
            storedAuth = AuthInfo(
                userId = 1,
                userEmail = "test@email.com",
                authToken = "fake-token",
            )
        ),
        matchService = FakeMatchService(),
        lobbySseService = FakeLobbySseService(),
    ) {

        private val _state = MutableStateFlow(initialState)
        override val currentState: StateFlow<LobbiesScreenState> = _state

        fun setState(state: LobbiesScreenState) {
            _state.value = state
        }

        var startCreatingLobbyCalled = false
        var cancelCreationCalled = false
        var leaveLobbyCalledWith: Int? = null
        var loadLobbiesCalled = false

        override fun startCreatingLobby() {
            startCreatingLobbyCalled = true
        }

        override fun onCancelCreation() {
            cancelCreationCalled = true
        }

        override fun leaveLobby(lobbyId: Int) {
            leaveLobbyCalledWith = lobbyId
        }

        override fun loadLobbies(loadingMessage: String?) {
            loadLobbiesCalled = true
        }

        var joinLobbyCalledWith: Int? = null

        override fun joinLobby(lobbyId: Int) {
            joinLobbyCalledWith = lobbyId
        }


    }


}







