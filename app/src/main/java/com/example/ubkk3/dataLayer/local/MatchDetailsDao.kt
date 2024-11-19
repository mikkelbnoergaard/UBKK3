package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ubkk3.match.MatchDetails

@Dao
interface MatchDetailsDao {
    @Insert
    suspend fun insert(matchDetails: MatchDetails)

    @Query("SELECT * FROM matchdetails WHERE tournamentId = :tournamentId")
    suspend fun getMatchesByTournamentId(tournamentId: Int): List<MatchDetails>
}