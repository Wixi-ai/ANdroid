package com.example.betpro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BetDao {
    @Insert
    suspend fun insert(bet: BetEntity)

    @Query("SELECT * FROM bets ORDER BY date DESC")
    fun getAllBets(): Flow<List<BetEntity>>

    @Query("DELETE FROM bets")
    suspend fun deleteAll()
}