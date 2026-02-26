package com.example.myapplication.profile.model

import kotlinx.serialization.Serializable

@Serializable
data class InviteOutputModel(
    val code: String,
    val createdBy: String,
)
