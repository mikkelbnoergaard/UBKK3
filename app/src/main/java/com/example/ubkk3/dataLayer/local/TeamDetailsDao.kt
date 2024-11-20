package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ubkk3.match.TeamDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDetailsDao {
    @Insert
    suspend fun insert(teamDetails: TeamDetails): Long

    @Query("SELECT id FROM teamdetails WHERE matchId = :matchId LIMIT 1")
    suspend fun getTeamIdByMatchId(matchId: Int): Int

    @Query("SELECT * FROM teamdetails WHERE matchId = :matchId")
    suspend fun getTeamsByMatchId(matchId: Int): List<TeamDetails>

    @Update
    suspend fun update(teamDetails: TeamDetails)
}