package com.example.betpro

import java.util.Date

data class Bet(
    val amount: Double,
    val odds: Double,
    val isWin: Boolean,
    val date: Date,
    val bookmaker: String
)