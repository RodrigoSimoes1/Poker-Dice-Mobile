package com.example.myapplication.profile

import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.login.domain.AuthService
import com.example.myapplication.login.domain.LogoutUseCase
import com.example.myapplication.profile.domain.UserService
import com.example.myapplication.profile.model.InviteOutputModel
import com.example.myapplication.profile.model.UserOutputModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthInfoRepo(
    initialAuthInfo: AuthInfo? = null,
) : AuthInfoRepo {

    private val _authInfo = MutableStateFlow<AuthInfo?>(initialAuthInfo)
    override val authInfo: Flow<AuthInfo?> = _authInfo

    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
        _authInfo.value = authInfo
    }

    override suspend fun getAuthInfo(): AuthInfo? = _authInfo.value

    override suspend fun clearAuthInfo() {
        _authInfo.value = null
    }
}
class FakeUserService(
    private val users: List<UserOutputModel> = emptyList(),
    private val shouldFail: Boolean = false,
    private val shouldThrow: Boolean = false
) : UserService {

    var callCount = 0

    override suspend fun getAllUsers(): Result<List<UserOutputModel>> {
        callCount++

        if (shouldThrow) {
            throw RuntimeException("Fake exception thrown")
        }

        return if (shouldFail) {
            Result.failure(Exception("Fake network error"))
        } else {
            Result.success(users)
        }
    }

    override suspend fun getUserById(userId: Int): Result<UserOutputModel> {
        val user = users.find { it.id == userId }
        return user?.let { Result.success(it) }
            ?: Result.failure(Exception("User not found"))
    }

    override suspend fun createInvite(token: String): Result<InviteOutputModel> {
        //fake invite
        return Result.success(InviteOutputModel(code = "invite_test", createdBy = "John Test"))
    }
}
class FakeLogoutUseCase : LogoutUseCase {
    var callCount = 0

    override suspend fun invoke(service: AuthService, repo: AuthInfoRepo) {
        callCount++
    }
}

abstract class FakeAuthService : AuthService {

    var logoutCalled = 0

    suspend fun logout(): Result<Unit> {
        logoutCalled++
        return Result.success(Unit)
    }
}

