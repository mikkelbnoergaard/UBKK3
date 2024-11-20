package com.example.ubkk3.dataLayer.local

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TournamentRepository @Inject constructor(
    private val tournamentDao: TournamentDao,
    private val matchDetailsDao: MatchDetailsDao,
    private val teamDetailsDao: TeamDetailsDao,
    private val playerDao: PlayerDao
){
    suspend fun createTournament(tournament: Tournament): Long {
        return withContext(Dispatchers.IO) {
            tournamentDao.insert(tournament)
        }
    }

    suspend fun createEmptyMatch(tournamentId: Int): Long {
        val match = MatchDetails(
            tournamentId = tournamentId,
            team1Won = false,
            team2Won = false,
            team1Id = 0,
            team2Id = 0,
            round = ""
        )
        return withContext(Dispatchers.IO) {
            matchDetailsDao.insert(match)
        }
    }

    suspend fun createEmptyTeam(matchId: Int): Long {
        val team = TeamDetails(
            matchId = matchId,
            teamName = "",
        )
        return withContext(Dispatchers.IO) {
            teamDetailsDao.insert(team)
        }
    }

    suspend fun createEmptyPlayer(teamId: Int): Long {
        val player = Player(
            teamId = teamId,
            name = "",
            email = ""
        )
        return withContext(Dispatchers.IO) {
            playerDao.insert(player)
        }
    }

    suspend fun getTournamentIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            tournamentDao.getTournamentIdByName(name)
        }
    }

    suspend fun finalizeTournament(tournament: Tournament, matches: List<MatchDetails>, teams: List<TeamDetails>, players: List<Player>) {
        withContext(Dispatchers.IO) {
            matches.forEach { matchDetailsDao.update(it) }
            teams.forEach { teamDetailsDao.update(it) }
            players.forEach { playerDao.update(it) }
        }
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

    suspend fun getMatchesByTournamentId(tournamentId: Int): List<MatchDetails> {
        return withContext(Dispatchers.IO) {
            matchDetailsDao.getMatchesByTournamentId(tournamentId)
        }
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

    suspend fun getTeamsByMatchId(matchId: Int): List<TeamDetails> {
        return withContext(Dispatchers.IO) {
            teamDetailsDao.getTeamsByMatchId(matchId)
        }
    }

    suspend fun getPlayersByTeamId(teamId: Int): List<Player> {
        return withContext(Dispatchers.IO) {
            playerDao.getPlayersByTeamId(teamId)
        }
    }
}