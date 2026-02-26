package com.example.myapplication.match

import com.example.myapplication.domainlogic.Dice
import com.example.myapplication.domainlogic.DiceFace
import com.example.myapplication.domainlogic.HandRank
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.match.domain.MatchService
import com.example.myapplication.match.domain.MatchSseService
import com.example.myapplication.match.model.input.RollDiceInputModel
import com.example.myapplication.match.model.output.CurrentTurnOutputModel
import com.example.myapplication.match.model.output.MatchEvent
import com.example.myapplication.match.model.output.MatchOutputModel
import com.example.myapplication.match.model.output.MatchStatusOutputModel
import com.example.myapplication.match.model.output.PlayerOutputModel
import com.example.myapplication.profile.model.UserOutputModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/* ---------------------------------------------------------
 * Fake Auth Repo
 * --------------------------------------------------------- */

class FakeAuthInfoRepo : AuthInfoRepo {

    private val _authInfo =
        MutableStateFlow<AuthInfo?>(
            AuthInfo(
                userId = 1,
                userEmail = "test@email.com",
                authToken = "fake-token",
            )
        )

    override val authInfo: Flow<AuthInfo?> = _authInfo

    override suspend fun getAuthInfo(): AuthInfo? =
        _authInfo.value

    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
        _authInfo.value = authInfo
    }

    override suspend fun clearAuthInfo() {
        _authInfo.value = null
    }
}

/* ---------------------------------------------------------
 * Fake Match Service
 * --------------------------------------------------------- */

class FakeMatchService : MatchService {

    override suspend fun getMatchStatus(
        token: String,
        lobbyId: Int,
    ): Result<MatchStatusOutputModel> =
        Result.success(
            MatchStatusOutputModel(
                id = 1,
                lobbyId = lobbyId,
                isFinished = false,
                players = listOf(
                    fakePlayer(1, "John Test"),
                    fakePlayer(2, "Alice Test"),
                ),
            )
        )

    override suspend fun startMatch(
        token: String,
        lobbyId: Int,
    ): Result<MatchOutputModel> =
        Result.success(
            MatchOutputModel(
                id = 1,
                lobbyId = lobbyId,
                isFinished = false,
                players = listOf<UserOutputModel>(
                    UserOutputModel(id = 1, name = "John Test", email = "test@email.com", balance = 100, gamesPlayed = 10, gamesWon = 5),
                    UserOutputModel(id = 1, name = "Alice Test", email = "testing@email.com", balance = 100, gamesPlayed = 10, gamesWon = 5),
                ),
            )
        )

    override suspend fun rollDice(
        token: String,
        lobbyId: Int,
        input: RollDiceInputModel,
    ): Result<CurrentTurnOutputModel> =
        Result.success(
            CurrentTurnOutputModel(
                userId = 1,
                userName = "Alice",
                pointsEarned = 10,
                dices = List(5) { Dice(it + 1, DiceFace.ACE) },
                handRank = HandRank.ONE_PAIR,
                triesLeft = 2,
            )
        )

    override suspend fun acceptPlay(
        token: String,
        lobbyId: Int,
    ): Result<MatchOutputModel> =
        Result.success(
            MatchOutputModel(
                id = 1,
                lobbyId = lobbyId,
                isFinished = false,
                players = listOf<UserOutputModel>(
                    UserOutputModel(id = 1, name = "John Test", email = "test@email.com", balance = 100, gamesPlayed = 10, gamesWon = 5),
                    UserOutputModel(id = 1, name = "Alice Test", email = "testing@email.com", balance = 100, gamesPlayed = 10, gamesWon = 5),
                ),
            )
        )

    private fun fakePlayer(
        id: Int,
        name: String,
    ) =
        PlayerOutputModel(
            id = id,
            name = name,
            email = "$name@test.com",
            balance = 100,
            gamesPlayed = 0,
            gamesWon = 0,
            playerHand = null,
        )
}


/* ---------------------------------------------------------
 * Fake SSE Service
 * --------------------------------------------------------- */

class FakeMatchSseService : MatchSseService {

    private val events = MutableSharedFlow<MatchEvent>()

    override suspend fun subscribeToMatch(matchId: Int): Flow<MatchEvent> =
        events.asSharedFlow()

    suspend fun emit(event: MatchEvent) {
        events.emit(event)
    }
}

