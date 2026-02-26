package com.example.myapplication.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myapplication.DependenciesContainer
import com.example.myapplication.about.AboutActivity
import com.example.myapplication.login.domain.performLogin
import com.example.myapplication.login.domain.performRegister
import com.example.myapplication.main.MainActivity
import kotlin.getValue

class AuthActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels {
        val diContainer = application as DependenciesContainer
        AuthViewModel.getFactory(
            authService = diContainer.authService,
            authRepo = diContainer.authInfoRepo,
            loginUseCase = ::performLogin,
            registerUseCase = ::performRegister,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AuthScreen(
                    onNavigate = { intent ->
                        when (intent) {
                            AuthScreenNavigationIntent.NavigateBack -> finish()
                            AuthScreenNavigationIntent.AboutScreenNavigationIntent -> navigateToAbout()
                            AuthScreenNavigationIntent.MainScreenNavigationIntent -> navigateToMain()
                        }
                    },
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }

    private fun navigateToAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
