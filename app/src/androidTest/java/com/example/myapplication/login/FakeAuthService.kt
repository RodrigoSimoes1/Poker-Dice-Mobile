package com.example.myapplication.login

import com.example.myapplication.login.domain.*
import com.example.myapplication.login.model.output.LoginOutputModel
import com.example.myapplication.profile.model.UserOutputModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthService(
    private val loginShouldSucceed: Boolean = true,
    private val registerShouldSucceed: Boolean = true
) : AuthService {

    var loginCallCount = 0
    var registerCallCount = 0

    override suspend fun login(email: String, password: String): Result<LoginOutputModel> {
        loginCallCount++
        return if (loginShouldSucceed)
            Result.success(LoginOutputModel("TEST_TOKEN"))
        else
            Result.failure(RuntimeException("Login failed"))
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        inviteCode: String
    ): Result<Unit> {
        registerCallCount++
        return if (registerShouldSucceed)
            Result.success(Unit)
        else
            Result.failure(RuntimeException("Register failed"))
    }

    override suspend fun logout(token: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun fetchMe(token: String): Result<UserOutputModel> =
        Result.success(
            UserOutputModel(
                id = 1,
                name = "Test User",
                email = "test@email.com",
                gamesPlayed = 0,
                gamesWon = 0,
                balance = 0
            )
        )
}

class FakeAuthRepo : AuthInfoRepo {
    private val flow = MutableStateFlow<AuthInfo?>(null)

    override val authInfo: Flow<AuthInfo?> = flow

    override suspend fun getAuthInfo(): AuthInfo? = flow.value

    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
        flow.value = authInfo
    }

    override suspend fun clearAuthInfo() {
        flow.value = null
    }
}

class FakeLoginUseCase(
    private val shouldSucceed: Boolean = true
) : LoginUseCase {

    var callCount = 0

    override suspend fun invoke(
        credentials: UserCredentials,
        service: AuthService,
        repo: AuthInfoRepo,
    ): AuthInfo {

        callCount++

        //add some delay to see a state transition
        delay(50)

        if (!shouldSucceed) {
            throw RuntimeException("Login failed")
        }

        return AuthInfo(
            authToken = "TEST_TOKEN",
            userEmail = credentials.email,
            userId = 0,
        )
    }
}


class FakeRegisterUseCase(
    private val shouldSucceed: Boolean = true
) : RegisterUseCase {

    var callCount = 0

    override suspend fun invoke(
        data: RegisterData,
        service: AuthService,
        repo: AuthInfoRepo,
    ): AuthInfo {

        callCount++

        //add some delay to see a state transition
        delay(50)

        if (!shouldSucceed) {
            throw RuntimeException("Register failed")
        }

        return AuthInfo(
            authToken = "REGISTER_TEST_TOKEN",
            userEmail = data.email,
            userId = 0,
        )
    }
}

