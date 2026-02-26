package com.example.myapplication.shop.domain

import com.example.myapplication.profile.model.UserOutputModel
import com.example.myapplication.shop.model.BalancePurchaseInputModel

interface BalanceService {
    suspend fun addBalance(
        token: String,
        code: BalancePurchaseInputModel,
    ): Result<UserOutputModel>
}
