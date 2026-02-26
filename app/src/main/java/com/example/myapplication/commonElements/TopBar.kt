package com.example.myapplication.commonElements

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

const val BACK_BUTTON_TAG = "back_button"
const val INFO_BUTTON_TAG = "info_button"
const val PROFILE_BUTTON_TAG = "profile_button"
const val TITLE_TEXT_TAG = "title_text"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String = "",
    onBackIntent: (() -> Unit)? = null,
    onInfoIntent: (() -> Unit)? = null,
    onProfileIntent: (() -> Unit)? = null,
) {
    TopAppBar(
        title = { Text(text = title, modifier = Modifier.testTag(tag = TITLE_TEXT_TAG)) },
        navigationIcon = {
            onBackIntent?.let {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable(onClick = it).testTag(tag = BACK_BUTTON_TAG),
                )
            }
            onProfileIntent?.let {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.clickable(onClick = it).testTag(tag = PROFILE_BUTTON_TAG),
                )
            }
        },
        actions = {
            onInfoIntent?.let {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "About",
                    modifier = Modifier.clickable(onClick = it).testTag(tag = INFO_BUTTON_TAG),
                )
            }
        },
    )
}
