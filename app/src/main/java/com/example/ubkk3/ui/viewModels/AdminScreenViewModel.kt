package com.example.ubkk3.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.ubkk3.dataLayer.local.TournamentRepository
import com.example.ubkk3.state.AdminState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.generateTournament.generateTournament
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.ui.event.AdminEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminScreenViewModel(
    private val repository: TournamentRepository
): ViewModel() {

    private val _tournaments = repository.observeAll()

    private val _adminState = MutableStateFlow(AdminState())
    val adminState = combine(_adminState, _tournaments) { adminState, tournaments ->
        adminState.copy(
            tournaments = tournaments
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminState())

    fun onEvent(event: AdminEvent) {
        when(event) {
            is AdminEvent.SaveTournamentInDatabase -> {
                val tournamentObject = Tournament(
                    tournamentName = adminState.value.createTournamentName,
                    isActive = true,
                )

                if(tournamentObject.tournamentName.isEmpty()){
                    println("Tournament name is empty")
                    return
                }

                viewModelScope.launch {
                    repository.createTournament(tournamentObject)
                    val tournamentId = repository.getTournamentById(tournamentObject.id).firstOrNull()?.id ?: return@launch

                    adminState.value.createTournamentMatches.forEach { match ->
                        repository.insertMatchDetails(match.copy(tournamentId = tournamentId))
                    }
                    val matchList = repository.getMatchesByTournamentId(tournamentObject.id)

                    adminState.value.createTournamentTeams.forEach { team ->
                        matchList.forEach { match ->
                            repository.insertTeamDetails(team.copy(matchId = match.id))
                        }
                    }

                    var teamsList = mutableListOf<TeamDetails>()

                    matchList.forEach { match ->
                        teamsList = (teamsList + repository.getTeamsByMatchId(match.id)) as MutableList<TeamDetails>
                    }
                    teamsList.forEach { team ->
                        repository.insertPlayer(player.copy(teamId = player.id))
                    }

                    matchList.forEach { match ->
                        val teams = repository.getTeamsByMatchId(match.id)
                        teams.forEach { team ->
                            repository.insertPlayer(player.copy(teamId = team.id))
                        }

                    adminState.value.createTournamentPlayers.forEach { player ->
                        repository.insertPlayer(player.copy(teamId = teamId))
                    }

                    adminState.value.createTournamentTeams.forEach { team ->
                        val teamDetails = team.copy(tournamentId = tournamentId)
                        repository.insertTeamDetails(teamDetails)
                        val teamId = repository.getTeamsByTournamentId(tournamentId).first { it.teamName == team.teamName }.id
                        team.players.forEach { player ->
                            repository.insertPlayer(player.copy(teamId = teamId))
                        }
                    }

                    adminState.value.createTournamentMatches.forEach { match ->
                        val matchDetails = match.copy(tournamentId = tournamentId)
                        repository.insertMatchDetails(matchDetails)
                    }
                }

                _adminState.update { it.copy(
                    createTournamentName = "",
                    createTournamentMatches = emptyList()
                ) }
            }

            is AdminEvent.SetTournamentTitle -> {
                _adminState.update { it.copy(
                    createTournamentName = event.title
                ) }
            }

            is AdminEvent.AddTeam -> {
                _adminState.update { it.copy(
                    createTournamentTeams = _adminState.value.createTournamentTeams + event.team
                ) }
            }

            is AdminEvent.GenerateMatches -> {
                _adminState.update { it.copy(
                    createTournamentMatches = generateTournament(_adminState.value.createTournamentTeams)
                ) }
            }

            is AdminEvent.UpdateTournamentActivityStatus -> {
                viewModelScope.launch {
                    repository.updateTournamentActivityStatus(event.tournament)
                }
            }

            is AdminEvent.AddTestTeams -> {

                val testTeams = listOf(
                    TeamDetails(tournamentId = 0, teamName = "Team A"),
                    TeamDetails(tournamentId = 0, teamName = "Team B"),
                    TeamDetails(tournamentId = 0, teamName = "Team C"),
                    TeamDetails(tournamentId = 0, teamName = "Team D")
                )

                _adminState.update { it.copy(
                    createTournamentTeams = it.createTournamentTeams + testTeams
                ) }
            }

            is AdminEvent.DeleteTeam -> {
                _adminState.update { it.copy(
                    createTournamentTeams = _adminState.value.createTournamentTeams.filter { it != event.team }
                ) }
            }
            is AdminEvent.UpdateTeam -> {
                _adminState.update { it.copy(
                    createTournamentTeams = _adminState.value.createTournamentTeams.map { if (it == event.updatedTeam) event.updatedTeam else it }
                ) }
            }
        }
    }
}