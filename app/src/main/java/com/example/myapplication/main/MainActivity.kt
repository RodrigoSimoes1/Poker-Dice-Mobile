package com.example.myapplication.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myapplication.about.AboutActivity
import com.example.myapplication.lobbies.LobbiesActivity
import com.example.myapplication.profile.ProfileActivity
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.Companion.padding(innerPadding),
                        onNavigate = { intent ->
                            when (intent) {
                                MainScreenNavigationIntent.NavigateToAbout -> navigateToAbout()
                                MainScreenNavigationIntent.NavigateToProfile -> navigateToProfile()
                                MainScreenNavigationIntent.NavigateToLobbys -> navigateToLobbys()
                            }
                        },
                    )
                }
            }
        }
    }

    private fun navigateToAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToLobbys() {
        val intent = Intent(this, LobbiesActivity::class.java)
        startActivity(intent)
    }
}
