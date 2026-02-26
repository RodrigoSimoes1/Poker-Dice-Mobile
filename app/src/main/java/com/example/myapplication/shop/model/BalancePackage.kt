package com.example.myapplication.shop.model

data class BalancePackage(
    val packageCode: String,
    val realMoney: Int,
    val coin: Int,
    val isPopular: Boolean = false,
) {
    companion object {
        val STARTER_PACK = BalancePackage("SP", 1, 500)
        val COIN_FEAST = BalancePackage("CF", 5, 3000)
        val TREASURE_CHEST = BalancePackage("TC", 20, 15000, isPopular = true)
        val MONEY_RAIN = BalancePackage("MR", 50, 45000)
        val I_HAVE_A_PROBLEM = BalancePackage("IHP", 100, 125000)

        fun getAllPackages() =
            listOf(
                STARTER_PACK,
                COIN_FEAST,
                TREASURE_CHEST,
                MONEY_RAIN,
                I_HAVE_A_PROBLEM,
            )
    }
}
