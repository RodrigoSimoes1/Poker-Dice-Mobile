package com.example.myapplication.shop

import ShopScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myapplication.DependenciesContainer
import com.example.myapplication.ui.theme.MyApplicationTheme

class ShopActivity : ComponentActivity() {
    private val shopViewModel: ShopViewModel by viewModels {
        ShopViewModel.getFactory(
            service = (application as DependenciesContainer).balanceService,
            authRepo = (application as DependenciesContainer).authInfoRepo,
            userService = (application as DependenciesContainer).userService,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                ShopScreen(
                    viewModel = shopViewModel,
                    onNavigateBack = { finish() },
                )
            }
        }
    }
}
