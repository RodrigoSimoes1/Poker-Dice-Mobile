package com.example.myapplication.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.commonElements.TopBar
import com.example.myapplication.shop.model.BalancePackage
import com.example.myapplication.ui.theme.Dimensions
import com.example.myapplication.R

const val OVERALL_SHOP_SCREEN_TAG = "overall_shop_screen"
const val SHOP_MAIN_COLUMN_TAG = "shop_main_column"
const val SHOP_SCREEN_CARD_TAG = "shop_screen_card"
const val SHOP_CURRENT_BALANCE_TAG = "shop_current_balance"
const val SHOP_NUMERIC_BALANCE_TAG = "shop_numeric_balance"
const val SHOP_BALANCE_ICON_TAG = "shop_balance_icon"
const val SHOP_PACKAGE_CHOSE_TAG = "shop_package_chose"
const val BUNDLES_TAG = "bundles"

enum class ShopScreenNavigationIntent {
    NavigateBack,
}

@Composable
fun ShopView(
    modifier: Modifier = Modifier,
    onNavigate: (ShopScreenNavigationIntent) -> Unit = {},
    currentBalance: Int,
    onPurchase: (BalancePackage) -> Unit = {},
    isPurchasing: Boolean = false,
) {
    Scaffold(
        topBar = {
            TopBar(
                onBackIntent = { onNavigate(ShopScreenNavigationIntent.NavigateBack) },
            )
        },
        modifier = Modifier.testTag(OVERALL_SHOP_SCREEN_TAG),
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing),
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(Dimensions.screenPadding)
                    .verticalScroll(rememberScrollState())
                    .testTag(SHOP_MAIN_COLUMN_TAG),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().testTag(SHOP_SCREEN_CARD_TAG),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.padding16),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.current_money_with_dots_text),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag(SHOP_CURRENT_BALANCE_TAG),


                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimensions.size24).testTag(SHOP_BALANCE_ICON_TAG),
                        )
                        Text(
                            text = currentBalance.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag(SHOP_NUMERIC_BALANCE_TAG),
                        )
                    }
                }
            }

            Text(
                text = stringResource(id = R.string.chose_your_packet_text),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag(SHOP_PACKAGE_CHOSE_TAG),
            )

            BalancePackage.getAllPackages().forEach { pkg ->
                BalancePackageCard(
                    balancePackage = pkg,
                    onPurchase = { onPurchase(pkg) },
                    isPurchasing = isPurchasing,
                    testTag = BUNDLES_TAG
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.sectionSpacingVertical))
        }
    }
}

@Composable
private fun BalancePackageCard(
    balancePackage: BalancePackage,
    onPurchase: () -> Unit,
    isPurchasing: Boolean,
    testTag:String,
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (balancePackage.isPopular) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = if (balancePackage.isPopular) Dimensions.elevation8 else Dimensions.elevation2,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.padding16),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimensions.size32),
                        )
                        Text(
                            text = balancePackage.coin.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        if (balancePackage.isPopular) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Popular",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier =
                                    Modifier
                                        .padding(start = Dimensions.padding8)
                                        .size(Dimensions.size20),
                            )
                        }
                    }

                    Text(
                        text = getPackageName(balancePackage.packageCode),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                Button(
                    onClick = onPurchase,
                    enabled = !isPurchasing,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (balancePackage.isPopular) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.secondary
                                },
                        ),
                    modifier = Modifier.testTag("PURCHASE_BUTTON_${balancePackage.packageCode}")
                ) {
                    if (isPurchasing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Dimensions.size20),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = Dimensions.strokeWidth2,
                        )
                    } else {
                        Text("€${balancePackage.realMoney}")
                    }
                }
            }

            if (balancePackage.isPopular) {
                Spacer(modifier = Modifier.height(Dimensions.height4))
                Text(
                    text = stringResource(id = R.string.best_value_text),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun getPackageName(code: String): String =
    when (code) {
        "SP" -> stringResource(id = R.string.starter_pack_text)
        "CF" -> stringResource(id = R.string.coin_feast_text)
        "TC" -> stringResource(id = R.string.treasure_chest_text)
        "MR" -> stringResource(id = R.string.money_rain_text)
        "IHP" -> stringResource(id = R.string.i_have_a_problem_text)
        else -> ""
    }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShopScreenPreview() {
    ShopView(
        currentBalance = 250,
        isPurchasing = false,
    )
}
