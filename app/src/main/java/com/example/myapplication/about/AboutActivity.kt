package com.example.myapplication.about

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.net.toUri
import com.example.myapplication.ui.theme.MyApplicationTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                AboutScreen(
                    onNavigate = { intent ->
                        when (intent) {
                            is AboutScreenNavigationIntent.NavigateBack -> finish()
                            is AboutScreenNavigationIntent.Email -> navigateToEmail(intent.subject)
                            is AboutScreenNavigationIntent.Browser -> navigateToBrowser(intent.destination)
                        }
                    },
                )
            }
        }
    }

    private fun navigateToEmail(subject: String) {
        val intent =
            Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, EMAILS)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
        startActivity(intent)
    }

    private fun navigateToBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }
}
