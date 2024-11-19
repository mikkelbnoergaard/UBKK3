package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ubkk3.match.Player

@Dao
interface PlayerDao {
    @Insert
    suspend fun insert(player: Player)

    @Query("SELECT * FROM player WHERE teamId = :teamId")
    suspend fun getPlayersByTeamId(teamId: Int): List<Player>
}