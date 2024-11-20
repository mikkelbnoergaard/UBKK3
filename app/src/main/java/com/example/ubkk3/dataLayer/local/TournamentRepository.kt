package com.example.ubkk3.dataLayer.local

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TournamentRepository @Inject constructor(
    private val tournamentDao: TournamentDao,
    private val matchDetailsDao: MatchDetailsDao,
    private val teamDetailsDao: TeamDetailsDao,
    private val playerDao: PlayerDao
){
    suspend fun createTournament(tournament: Tournament) {
        tournamentDao.createTournament(tournament)
    }

    suspend fun deleteTournament(tournament: Tournament) {
        tournamentDao.deleteTournament(tournament)
    }

    fun observeAll(): Flow<List<Tournament>> {
        return tournamentDao.observeAll()
    }

    fun getTournamentById(tournamentId: Int): Flow<Tournament> {
        return tournamentDao.getTournamentById(tournamentId)
    }

    suspend fun getTeamsByMatchId(matchId: Int): Flow<TeamDetails> {
        return teamDetailsDao.getTeamsByMatchId(matchId)
    }

    suspend fun getTeamsByTournamentId(tournamentId: Int): List<TeamDetails> {
        return teamDetailsDao.getTeamsByTournamentId(tournamentId)
    }

    suspend fun getMatchesByTournamentId(tournamentId: Int): List<MatchDetails> {
        return matchDetailsDao.getMatchesByTournamentId(tournamentId)
    }

    suspend fun updateTournamentActivityStatus(tournament: Tournament) {
        tournamentDao.updateTournamentActivityStatus(tournament.tournamentName)
    }

    suspend fun updateMatchWinner(tournamentId: Int, matchId: Int, winningTeam: Int) {
        if (winningTeam == 1) {
            tournamentDao.updateMatchWinnerToTeam1(tournamentId, matchId)
        } else {
            tournamentDao.updateMatchWinnerToTeam2(tournamentId, matchId)
        }
    }

    suspend fun insertTeamDetails(teamDetails: TeamDetails) {
        teamDetailsDao.insert(teamDetails)
    }

    suspend fun insertMatchDetails(matchDetails: MatchDetails) {
        matchDetailsDao.insert(matchDetails)
    }

    suspend fun insertPlayer(player: Player) {
        playerDao.insert(player)
    }
}