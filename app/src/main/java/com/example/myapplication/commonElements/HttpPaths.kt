package com.example.myapplication.commonElements
import com.example.myapplication.BuildConfig
object HttpPaths {
    const val BASE_URL = BuildConfig.API_BASE_URL
    const val AUTH_PATH = "${BASE_URL}/api/auth"
    const val USERS_PATH = "${BASE_URL}/api/users"

    // Auth
    const val LOGIN_PATH = "$AUTH_PATH/login"
    const val REGISTER_PATH = "$AUTH_PATH/register"
    const val LOGOUT_PATH = "$AUTH_PATH/logout"
    const val ME_PATH = "${BASE_URL}/api/auth/me"
    const val LOBBY_PATH = "${BASE_URL}/api/lobbies"
    const val LOBBY_BY_USER = "${BASE_URL}/api/lobbies/users/{userId}"
    const val INVITE_PATH = "${BASE_URL}/api/invite"
    const val SSE_LOBBYS = "${BASE_URL}/api/sse/global"
    const val SSE_LOBBY = "${BASE_URL}/api/sse/lobby"
    const val SSE_MATCH = "${BASE_URL}/api/sse/match/{matchId}"
    const val BALANCE_PATH = "${BASE_URL}/api/users/balance"

    const val START_PATH = "${BASE_URL}/api/lobbies/{lobbiesId}/match/start"
    const val ROLL_PATH = "${BASE_URL}/api/lobbies/{lobbiesId}/match/roll"
    const val ACCEPT_PATH = "${BASE_URL}/api/lobbies/{lobbiesId}/match/roll/accept"
    const val STATUS_PATH = "${BASE_URL}/api/lobbies/{lobbiesId}/match/status"
}
