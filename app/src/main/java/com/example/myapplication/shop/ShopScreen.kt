import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.myapplication.shop.ShopScreenNavigationIntent
import com.example.myapplication.shop.ShopScreenState
import com.example.myapplication.shop.ShopView
import com.example.myapplication.shop.ShopViewModel
import com.example.myapplication.R

const val SHOP_ERROR_BOX_TAG = "SHOP_ERROR_BOX_TAG"
const val SHOP_SCREEN_VIEW_TAG = "SHOP_SCREEN_VIEW_TAG"
const val SHOP_CIRCULAR_PROGRESS_INDICATOR_TAG = "SHOP_CIRCULAR_PROGRESS_INDICATOR_TAG"


@Composable
fun ShopScreen(
    viewModel: ShopViewModel,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is ShopScreenState.Idle,
        is ShopScreenState.Loading,
        -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag(SHOP_CIRCULAR_PROGRESS_INDICATOR_TAG))
            }
        }

        is ShopScreenState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize().testTag(SHOP_ERROR_BOX_TAG),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = currentState.message)
            }
        }

        is ShopScreenState.Success -> {
            ShopView(
                currentBalance = currentState.currentBalance,
                isPurchasing = currentState.isPurchasing,
                onNavigate = { intent ->
                    when (intent) {
                        ShopScreenNavigationIntent.NavigateBack -> onNavigateBack()
                    }
                },
                onPurchase = { pkg ->
                    viewModel.purchaseBalance(pkg)
                },
                modifier = Modifier.testTag(SHOP_SCREEN_VIEW_TAG),
            )
        }
    }
}
