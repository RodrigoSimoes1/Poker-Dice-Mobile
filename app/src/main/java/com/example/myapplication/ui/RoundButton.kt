package com.example.myapplication.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.myapplication.ui.theme.Dimensions

@Composable
fun RoundButton(
    onClick: () -> Unit,
    symbol: Char,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        modifier =
            modifier
                .padding(all = Dimensions.columnSpacing)
                .size(size = Dimensions.roundButtonSize)
                .clip(CircleShape),
    ) {
        Text(
            text = symbol.toString(),
            fontSize = Dimensions.buttonTextSize,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
