package com.example.ubkk3.dataLayer.local

import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TournamentRepository @Inject constructor(
    private val dataSource: TournamentDao
){
    suspend fun createTournament(tournament: Tournament) {
        dataSource.createTournament(tournament)
    }

    suspend fun deleteTournament(tournament: Tournament) {
        dataSource.deleteTournament(tournament)
    }

    fun observeAll(): Flow<List<Tournament>> {
        return dataSource.observeAll()
    }

    fun getTournamentById(tournamentId: Int): Tournament {
        return dataSource.getTournamentById(tournamentId)
    }

    suspend fun updateTournamentActivityStatus(tournament: Tournament) {
        dataSource.updateTournamentActivityStatus(tournament.tournamentName)
    }

    suspend fun updateMatchWinner(tournamentId: Int, matchId: Int, winningTeam: Int) {
        if (winningTeam == 1) {
            dataSource.updateMatchWinnerToTeam1(tournamentId, matchId)
        } else {
            dataSource.updateMatchWinnerToTeam2(tournamentId, matchId)
        }
    }
}