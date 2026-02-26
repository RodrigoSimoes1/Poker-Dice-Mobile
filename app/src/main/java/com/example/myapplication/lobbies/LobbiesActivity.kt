package com.example.myapplication.lobbies

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myapplication.DependenciesContainer
import com.example.myapplication.about.AboutActivity
import com.example.myapplication.match.MatchActivity
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlin.getValue

class LobbiesActivity : ComponentActivity() {
    private val viewModel: LobbiesViewModel by viewModels {
        LobbiesViewModel.getFactory(
            lobbyService = (application as DependenciesContainer).lobbyService,
            authRepo = (application as DependenciesContainer).authInfoRepo,
            lobbySseService = (application as DependenciesContainer).lobbySseService,
            matchService = (application as DependenciesContainer).matchService,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                LobbiesSelectionScreen(
                    viewModel = viewModel,
                    onNavigate = { intent ->
                        when (intent) {
                            LobbiesNavigationIntent.Back -> finish()
                            LobbiesNavigationIntent.About -> navigateToAbout()
                            LobbiesNavigationIntent.EnterMatch -> navigateToMatch()
                        }
                    },
                )
            }
        }
    }

    private fun navigateToAbout() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun navigateToMatch() {
        val currentState = viewModel.currentState.value
        var matchIdToPass = -1
        var hostIdToPass = -1

        when (currentState) {
            is LobbiesScreenState.NavigateToMatch -> {
                matchIdToPass = currentState.matchId
                hostIdToPass = currentState.hostId
            }
            is LobbiesScreenState.WaitingRoom -> {
                matchIdToPass = currentState.lobby.id
                hostIdToPass = currentState.lobby.host.id
            }
            else -> {}
        }
        if (matchIdToPass != -1) {
            val intent = Intent(this, MatchActivity::class.java)
            intent.putExtra("LOBBY_ID_EXTRA", matchIdToPass)
            intent.putExtra("HOST_ID_EXTRA", hostIdToPass)
            startActivity(intent)
        }
    }
}
