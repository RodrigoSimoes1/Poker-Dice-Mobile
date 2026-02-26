package com.example.myapplication

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.lobbies.domain.LobbyService
import com.example.myapplication.lobbies.domain.LobbySseService
import com.example.myapplication.lobbies.http.LobbyServiceHttp
import com.example.myapplication.lobbies.http.LobbySseServiceHttp
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.login.domain.AuthService
import com.example.myapplication.login.http.AuthServiceHttp
import com.example.myapplication.login.repository.AuthInfoPreferencesRepo
import com.example.myapplication.match.domain.MatchService
import com.example.myapplication.match.domain.MatchSseService
import com.example.myapplication.match.http.MatchServiceHttp
import com.example.myapplication.match.http.MatchSseServiceHttp
import com.example.myapplication.profile.domain.UserService
import com.example.myapplication.profile.http.UserServiceHttp
import com.example.myapplication.shop.domain.BalanceService
import com.example.myapplication.shop.http.BalanceServiceHttp
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Interface que define as dependências partilhadas pela aplicação.
 */
interface DependenciesContainer {
    val authService: AuthService

    val userService: UserService

    val authInfoRepo: AuthInfoRepo

    val lobbyService: LobbyService

    val lobbySseService: LobbySseService

    val balanceService: BalanceService

    val matchService: MatchService

    val matchSseService: MatchSseService
}

/**
 * Classe Application principal da app PokerDice.
 * Responsável por criar e disponibilizar as dependências globais.
 */
class PokerDiceApplication :
    Application(),
    DependenciesContainer {
    private val httpClient by lazy {
        HttpClient {
            expectSuccess = false
            install(plugin = ContentNegotiation) {
                json(
                    json =
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        },
                )
            }
            install(SSE)
            install(HttpTimeout)
        }
    }

    /**
     * The DataStore instance for storing authentication information.
     */
    private val ds: DataStore<Preferences> by preferencesDataStore(name = "auth_info")

    override val authService: AuthService by lazy {
        AuthServiceHttp(client = httpClient)
    }

    override val userService: UserService by lazy {
        UserServiceHttp(client = httpClient)
    }

    override val authInfoRepo: AuthInfoRepo by lazy {
        AuthInfoPreferencesRepo(store = ds)
    }

    override val lobbyService: LobbyService by lazy {
        LobbyServiceHttp(client = httpClient)
    }

    override val lobbySseService: LobbySseService by lazy {
        LobbySseServiceHttp(client = httpClient)
    }

    override val balanceService: BalanceService by lazy {
        BalanceServiceHttp(client = httpClient)
    }

    override val matchService: MatchService by lazy {
        MatchServiceHttp(client = httpClient)
    }

    override val matchSseService: MatchSseService by lazy {
        MatchSseServiceHttp(client = httpClient)
    }
}
