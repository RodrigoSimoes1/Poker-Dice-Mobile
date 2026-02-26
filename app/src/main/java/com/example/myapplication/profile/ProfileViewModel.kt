package com.example.myapplication.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.login.domain.AuthService
import com.example.myapplication.login.domain.LogoutUseCase
import com.example.myapplication.profile.domain.UserService
import com.example.myapplication.profile.model.InviteOutputModel
import com.example.myapplication.profile.model.UserOutputModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados possíveis do ecrã de perfil (Profile).
 */
sealed interface ProfileScreenState {
    object Idle : ProfileScreenState

    object Loading : ProfileScreenState

    data class Success(
        val user: UserOutputModel,
        val inviteCode: InviteOutputModel? = null,
    ) : ProfileScreenState

    data class Error(
        val message: String,
    ) : ProfileScreenState
}

/**
 * ViewModel responsável por carregar os dados do utilizador.
 */
class ProfileViewModel(
    private val service: UserService,
    private val authRepo: AuthInfoRepo,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    companion object {
        fun getFactory(
            service: UserService,
            authRepo: AuthInfoRepo,
            logoutUseCase: LogoutUseCase,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    ProfileViewModel(
                        service,
                        authRepo,
                        logoutUseCase,
                    ) as T
                } else {
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
        }
    }

    private val _state = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Idle)
    val state: StateFlow<ProfileScreenState> = _state.asStateFlow()

    init {
        loadUserByEmail()
    }

    /**
     * Carrega o utilizador com base no email (obtido após login).
     */
    fun loadUserByEmail() {
        if (_state.value is ProfileScreenState.Loading) return
        _state.value = ProfileScreenState.Loading

        viewModelScope.launch {
            try {
                val authInfo = authRepo.getAuthInfo()
                val email = authInfo?.userEmail

                if (email.isNullOrEmpty()) {
                    _state.value = ProfileScreenState.Error("Nenhuma sessão ativa.")
                    return@launch
                }

                val result = service.getAllUsers()
                if (result.isSuccess) {
                    val users = result.getOrThrow()
                    val foundUser = users.find { it.email == email }
                    if (foundUser != null) {
                        _state.value = ProfileScreenState.Success(foundUser)
                    } else {
                        _state.value = ProfileScreenState.Error("Utilizador não encontrado.")
                    }
                } else {
                    _state.value = ProfileScreenState.Error("Erro ao carregar utilizadores.")
                }
            } catch (e: Exception) {
                _state.value = ProfileScreenState.Error(e.message ?: "Erro desconhecido.")
            }
        }
    }

    fun generateInvite() {
        val currentState = _state.value
        if (currentState !is ProfileScreenState.Success) return

        viewModelScope.launch {
            val authInfo = authRepo.getAuthInfo() ?: return@launch

            service
                .createInvite(authInfo.authToken)
                .onSuccess { invite ->
                    _state.value = currentState.copy(inviteCode = invite)
                }.onFailure { e ->
                    println("Erro ao gerar convite: ${e.message}")
                }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                logoutUseCase(service as AuthService, authRepo)
            } catch (e: Exception) {
                println("Erro no logout API: ${e.message}")
            } finally {
                onLogoutComplete()
            }
        }
    }
}
