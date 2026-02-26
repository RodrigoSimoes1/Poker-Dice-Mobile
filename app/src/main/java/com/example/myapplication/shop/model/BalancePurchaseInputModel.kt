package com.example.myapplication.shop.model

import kotlinx.serialization.Serializable

@Serializable
data class BalancePurchaseInputModel(
    val packageCode: String,
)
