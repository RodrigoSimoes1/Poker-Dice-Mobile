package com.example.myapplication.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val title: String,
    val description: String,
    val solution: String,
)
