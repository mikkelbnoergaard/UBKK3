package com.example.ubkk3.dataLayer.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    @Insert
    suspend fun createTournament(tournament: Tournament)

    @Delete
    suspend fun deleteTournament(tournament: Tournament)

    @Query("SELECT * FROM tournament")
    fun observeAll(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournament WHERE id = :tournamentId")
    fun getTournamentById(tournamentId: Int): Tournament

    @Query("UPDATE tournament SET isActive = CASE WHEN isActive = 1 THEN 0 ELSE 1 END WHERE tournamentName = :tournamentName")
    fun updateTournamentActivityStatus(tournamentName: String)

    @Query("UPDATE matchdetails SET team1Won = 1 WHERE tournamentId = :tournamentId AND id = :matchId")
    fun updateMatchWinnerToTeam1(tournamentId: Int, matchId: Int)

    @Query("UPDATE matchdetails SET team2Won = 1 WHERE tournamentId = :tournamentId AND id = :matchId")
    fun updateMatchWinnerToTeam2(tournamentId: Int, matchId: Int)
}