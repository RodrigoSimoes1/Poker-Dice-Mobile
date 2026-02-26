package com.example.myapplication.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myapplication.DependenciesContainer
import com.example.myapplication.about.AboutActivity
import com.example.myapplication.login.AuthActivity
import com.example.myapplication.login.domain.performLogout
import com.example.myapplication.shop.ShopActivity
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlin.jvm.java

class ProfileActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModel.getFactory(
            service = (application as DependenciesContainer).userService,
            authRepo = (application as DependenciesContainer).authInfoRepo,
            logoutUseCase = ::performLogout,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigate = { intent ->
                        when (intent) {
                            ProfileScreenNavigationIntent.NavigateToHome -> finish()
                            ProfileScreenNavigationIntent.NavigateToAbout -> navigateToAbout()
                            ProfileScreenNavigationIntent.NavigateToLogin -> {
                                profileViewModel.logout {
                                    navigateToLogin()
                                }
                            }

                            ProfileScreenNavigationIntent.NavigateToShop -> navigateToShop()
                        }
                    },
                )
            }
        }
    }

    /**
     * Ao dar back na shop activity voltar ao profile
     * forçamos o ViewModel a ir buscar os dados atualizados ao servidor.
     */
    override fun onResume() {
        super.onResume()
        profileViewModel.loadUserByEmail()
    }

    private fun navigateToAbout() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, AuthActivity::class.java))
        finishAffinity()
    }

    private fun navigateToShop() {
        startActivity(Intent(this, ShopActivity::class.java))
    }
}
