package com.example.myapplication.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.profile.domain.UserService
import com.example.myapplication.shop.domain.BalanceService
import com.example.myapplication.shop.model.BalancePackage
import com.example.myapplication.shop.model.BalancePurchaseInputModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ShopScreenState {
    object Idle : ShopScreenState

    object Loading : ShopScreenState

    data class Success(
        val currentBalance: Int,
        val isPurchasing: Boolean = false,
    ) : ShopScreenState

    data class Error(
        val message: String,
    ) : ShopScreenState
}

class ShopViewModel(
    private val service: BalanceService,
    private val authRepo: AuthInfoRepo,
    private val userService: UserService,
) : ViewModel() {
    companion object {
        fun getFactory(
            service: BalanceService,
            authRepo: AuthInfoRepo,
            userService: UserService,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
                    ShopViewModel(service, authRepo, userService) as T
                } else {
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
        }
    }

    private val _state = MutableStateFlow<ShopScreenState>(ShopScreenState.Idle)
    val state: StateFlow<ShopScreenState> = _state.asStateFlow()

    init {
        loadUserBalance()
    }

    /**
     * Carrega o balance inicial.
     */
    fun loadUserBalance() {
        if (_state.value is ShopScreenState.Loading) return
        _state.value = ShopScreenState.Loading

        viewModelScope.launch {
            try {
                val authInfo = authRepo.getAuthInfo()
                val email = authInfo?.userEmail

                if (email.isNullOrEmpty()) {
                    _state.value = ShopScreenState.Error("Nenhuma sessão ativa.")
                    return@launch
                }

                val result = userService.getAllUsers()
                if (result.isSuccess) {
                    val users = result.getOrThrow()
                    val foundUser = users.find { it.email == email }
                    if (foundUser != null) {
                        _state.value =
                            ShopScreenState.Success(
                                currentBalance = foundUser.balance,
                                isPurchasing = false,
                            )
                    } else {
                        _state.value = ShopScreenState.Error("Utilizador não encontrado.")
                    }
                } else {
                    _state.value = ShopScreenState.Error("Erro ao carregar balance.")
                }
            } catch (e: Exception) {
                _state.value = ShopScreenState.Error(e.message ?: "Erro desconhecido.")
            }
        }
    }

    fun purchaseBalance(balancePackage: BalancePackage) {
        val currentState = _state.value

        if (currentState !is ShopScreenState.Success) return
        if (currentState.isPurchasing) return

        viewModelScope.launch {
            _state.value = currentState.copy(isPurchasing = true)

            val authInfo = authRepo.getAuthInfo()
            if (authInfo == null) {
                _state.value = currentState.copy(isPurchasing = false)
                return@launch
            }

            service
                .addBalance(
                    token = authInfo.authToken,
                    code = BalancePurchaseInputModel(balancePackage.packageCode),
                ).onSuccess { userOutput ->
                    val currentSuccessState = _state.value
                    if (currentSuccessState is ShopScreenState.Success) {
                        _state.value =
                            currentSuccessState.copy(
                                currentBalance = userOutput.balance,
                                isPurchasing = false,
                            )
                    }
                }.onFailure { e ->
                    val currentErrorState = _state.value
                    if (currentErrorState is ShopScreenState.Success) {
                        _state.value = currentErrorState.copy(isPurchasing = false)
                    }
                }
        }
    }
}
