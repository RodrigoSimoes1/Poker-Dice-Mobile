package com.example.myapplication.shop

import com.example.myapplication.domainlogic.User
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.login.domain.AuthInfoRepo
import com.example.myapplication.profile.domain.UserService
import com.example.myapplication.profile.model.InviteOutputModel
import com.example.myapplication.profile.model.UserOutputModel
import com.example.myapplication.shop.domain.BalanceService
import com.example.myapplication.shop.model.BalancePurchaseInputModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.toMutableList

class FakeAuthInfoRepo(
    initialAuthInfo: AuthInfo? = AuthInfo(
        userEmail = "test@email.com",
        authToken = "fake-token",
        userId = 0
    )
) : AuthInfoRepo {

    private val _authInfo = MutableStateFlow<AuthInfo?>(initialAuthInfo)
    override val authInfo: Flow<AuthInfo?> = _authInfo.asStateFlow()

    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
        _authInfo.value = authInfo
    }

    override suspend fun getAuthInfo(): AuthInfo? {
        return _authInfo.value
    }

    override suspend fun clearAuthInfo() {
        _authInfo.value = null
    }
}


class FakeUserService(
    initialUsers: List<UserOutputModel> = listOf(
        UserOutputModel(
            id = 0,
            name = "John Test",
            email = "test@email.com",
            balance = 100,
            gamesPlayed = 10,
            gamesWon = 5
        )
    )
) : UserService {

    private val users = initialUsers.toMutableList()

    override suspend fun getAllUsers(): Result<List<UserOutputModel>> {
        return Result.success(users.toList())
    }

    override suspend fun getUserById(userId: Int): Result<UserOutputModel> {
        val user = users.find { it.id == userId }
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(NoSuchElementException("User not found"))
        }
    }
    //here because we must implement all services
    override suspend fun createInvite(token: String): Result<InviteOutputModel> {
        if (token.isBlank()) {
            return Result.failure(IllegalStateException("Invalid token"))
        }

        return Result.success(
            InviteOutputModel(
                code = "FAKE-INVITE-CODE",
                createdBy = "John Test",
            )
        )
    }

    /* Helper for fakes */
    fun updateUserBalance(userId: Int, newBalance: Int) {
        val index = users.indexOfFirst { it.id == userId }
        if (index != -1) {
            users[index] = users[index].copy(balance = newBalance)
        }
    }
}


class FakeBalanceService(
    private val userService: FakeUserService,
    private val priceTable: Map<String, Int> = mapOf(
        "SMALL_TEST" to 50,
        "MEDIUM_TEST" to 100,
        "LARGE_TEST" to 250
    )
) : BalanceService {

    override suspend fun addBalance(
        token: String,
        code: BalancePurchaseInputModel
    ): Result<UserOutputModel> {
        if (token.isBlank()) {
            return Result.failure(IllegalStateException("Invalid token"))
        }

        val amount = priceTable[code.packageCode]
            ?: return Result.failure(IllegalArgumentException("Invalid package"))
        val user = userService.getAllUsers()
            .getOrElse { return Result.failure(it) }
            .first()

        val updatedUser = user.copy(
            balance = user.balance + amount
        )

        userService.updateUserBalance(user.id, updatedUser.balance)

        return Result.success(updatedUser)
    }
}