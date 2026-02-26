package com.example.myapplication.match

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myapplication.DependenciesContainer
import com.example.myapplication.lobbies.LobbiesActivity
import com.example.myapplication.ui.theme.MyApplicationTheme

class MatchActivity : ComponentActivity() {
    private val lobbyId by lazy { intent.getIntExtra("LOBBY_ID_EXTRA", -1) }
    private val hostId by lazy { intent.getIntExtra("HOST_ID_EXTRA", -1) }
    private val viewModel: MatchViewModel by viewModels {
        MatchViewModel.getFactory(
            matchService = (application as DependenciesContainer).matchService,
            authRepo = (application as DependenciesContainer).authInfoRepo,
            matchSseService = (application as DependenciesContainer).matchSseService,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (lobbyId == -1) {
            finish()
            return
        }

        viewModel.loadMatch(lobbyId, hostId)

        setContent {
            MyApplicationTheme {
                MatchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { intent ->
                        when (intent) {
                            MatchNavigationIntent.BackToLobbies -> navigateToLobbies()
                        }
                    },
                )
            }
        }
    }

    private fun navigateToLobbies() {
        startActivity(Intent(this, LobbiesActivity::class.java))
        finish()
    }
}
