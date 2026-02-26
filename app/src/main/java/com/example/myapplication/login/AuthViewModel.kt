package com.example.myapplication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.login.domain.AuthService
import com.example.myapplication.login.domain.LoginUseCase
import com.example.myapplication.login.domain.RegisterData
import com.example.myapplication.login.domain.RegisterUseCase
import com.example.myapplication.login.domain.UserCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Represents the state of the login screen.
 */
sealed interface AuthScreenState {
    object Idle : AuthScreenState

    // login
    data class LoginInProgress(
        val tentativeCredentials: UserCredentials,
    ) : AuthScreenState

    data class LoginSuccess(
        val authToken: String,
    ) : AuthScreenState

    // Register
    data class RegisterInProgress(
        val data: RegisterData,
    ) : AuthScreenState

    object RegisterSuccess : AuthScreenState

    // error
    data class AuthError(
        val errorMessage: String,
    ) : AuthScreenState
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val service: AuthService,
    private val authRepo: AuthInfoRepo,
) : ViewModel() {
    companion object {
        fun getFactory(
            loginUseCase: LoginUseCase,
            registerUseCase: RegisterUseCase,
            authService: AuthService,
            authRepo: AuthInfoRepo,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AuthViewModel(
                    loginUseCase,
                    registerUseCase,
                    authService,
                    authRepo,
                ) as T
        }
    }

    private val _currentState = MutableStateFlow<AuthScreenState>(AuthScreenState.Idle)
    val currentState: StateFlow<AuthScreenState> get() = _currentState.asStateFlow()

    fun login(credentials: UserCredentials) {
        if (_currentState.value is AuthScreenState.LoginInProgress ||
            _currentState.value is AuthScreenState.LoginSuccess
        ) {
            return
        }

        _currentState.value = AuthScreenState.LoginInProgress(credentials)

        viewModelScope.launch {
            _currentState.value =
                try {
                    val authInfo = loginUseCase(credentials, service, authRepo)
                    _currentState.value = AuthScreenState.LoginSuccess(authInfo.authToken)
                    AuthScreenState.LoginSuccess(authInfo.authToken)
                } catch (e: Exception) {
                    AuthScreenState.AuthError(e.message ?: "Erro de autenticação")
                }
        }
    }

    fun register(data: RegisterData) {
        if (_currentState.value is AuthScreenState.RegisterInProgress) return
        _currentState.value = AuthScreenState.RegisterInProgress(data)

        viewModelScope.launch {
            _currentState.value =
                try {
                    registerUseCase(data, service, authRepo)
                    AuthScreenState.RegisterSuccess
                } catch (e: Exception) {
                    AuthScreenState.AuthError(e.message ?: "Erro ao criar conta")
                }
        }
    }
}
