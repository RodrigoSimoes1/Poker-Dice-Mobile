package com.example.myapplication.domainlogic

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val balance: Double = 0.0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
)
