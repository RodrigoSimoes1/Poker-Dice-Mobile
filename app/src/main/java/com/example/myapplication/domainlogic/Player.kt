package com.example.myapplication.domainlogic

data class Player(
    val name: String,
    val balance: Int,
    val password: String,
) {
    fun alterBalance(change: Int): Player { // a player cannot have negative money
        val nawBalance = this.balance + change
        if (nawBalance > 0) {
            return this.copy(balance = nawBalance)
        } else {
            return this.copy(balance = 0)
        }
    }

    fun comparePassword(attempt: String): Boolean {
        if (this.password == attempt) {
            return true
        } else {
            return false
        }
    }
}
