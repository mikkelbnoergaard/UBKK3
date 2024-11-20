package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ubkk3.match.MatchDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDetailsDao {

    @Insert
    suspend fun insert(matchDetails: MatchDetails): Long

    @Query("SELECT * FROM matchdetails WHERE tournamentId = :tournamentId")
    suspend fun getMatchesByTournamentId(tournamentId: Int): List<MatchDetails>

    @Query("SELECT * FROM matchdetails WHERE id = :matchId")
    fun getMatchById(matchId: Int): Flow<MatchDetails>

    @Query("SELECT id FROM matchdetails WHERE tournamentId = :tournamentId LIMIT 1")
    suspend fun getMatchIdByTournamentId(tournamentId: Int): Int

    @Update
    suspend fun update(matchDetails: MatchDetails)
}