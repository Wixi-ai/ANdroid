package com.example.betpro

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bets")
data class BetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val odds: Double,
    val isWin: Boolean,
    val date: Date,
    val bookmaker: String
)