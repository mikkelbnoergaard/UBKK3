package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ubkk3.match.TeamDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDetailsDao {
    @Insert
    suspend fun insert(teamDetails: TeamDetails)

    @Query("SELECT * FROM teamdetails WHERE id = :tournamentId")
    suspend fun getTeamsByTournamentId(tournamentId: Int): List<TeamDetails>

    @Query("SELECT * FROM teamdetails WHERE matchId = :matchId")
    suspend fun getTeamsByMatchId(matchId: Int): Flow<TeamDetails>
}