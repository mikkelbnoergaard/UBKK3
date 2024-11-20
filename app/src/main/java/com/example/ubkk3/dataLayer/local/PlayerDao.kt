package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ubkk3.match.Player

@Dao
interface PlayerDao {
    @Insert
    suspend fun insert(player: Player): Long

    @Query("SELECT id FROM player WHERE teamId = :teamId LIMIT 1")
    suspend fun getPlayerIdByTeamId(teamId: Int): Int

    @Query("SELECT * FROM player WHERE teamId = :teamId")
    suspend fun getPlayersByTeamId(teamId: Int): List<Player>

    @Update
    suspend fun update(player: Player)
}