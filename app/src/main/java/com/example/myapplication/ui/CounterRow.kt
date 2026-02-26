package com.example.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.myapplication.ui.theme.Dimensions

@Composable
fun CounterRow(
    label: String,
    value: Int,
    onDec: () -> Unit,
    onInc: () -> Unit,
    decEnabled: Boolean,
    incEnabled: Boolean,
    testTag:String = "Default"
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.columnSpacing)
                .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = Dimensions.columnSpacing),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            RoundButton(
                symbol = '-',
                onClick = onDec,
                enabled = decEnabled,
            )

            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier
                        .padding(horizontal = Dimensions.counterValuePadding),
                textAlign = TextAlign.Center,
            )

            RoundButton(
                symbol = '+',
                onClick = onInc,
                enabled = incEnabled,
            )
        }
    }
}
