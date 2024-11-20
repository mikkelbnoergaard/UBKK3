package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ubkk3.match.MatchDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDetailsDao {
    @Insert
    suspend fun insert(matchDetails: MatchDetails)

    @Query("SELECT * FROM matchdetails WHERE tournamentId = :tournamentId")
    suspend fun getMatchesByTournamentId(tournamentId: Int): List<MatchDetails>

    @Query("SELECT * FROM matchdetails WHERE id = :matchId")
    suspend fun getMatchById(matchId: Int): Flow<MatchDetails>
}