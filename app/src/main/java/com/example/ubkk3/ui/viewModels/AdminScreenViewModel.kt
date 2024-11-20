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
                    val tournamentId = repository.createTournament(tournamentObject).toInt()

                    val matchIds = adminState.value.createTournamentMatches.map { match ->
                        repository.createEmptyMatch(tournamentId)
                    }

                    val teamIds = matchIds.flatMap { matchId ->
                        adminState.value.createTournamentTeams.map { team ->
                            repository.createEmptyTeam(matchId.toInt())
                        }
                    }

                    teamIds.forEach { teamId ->
                        adminState.value.createTournamentPlayers.forEach { player ->
                            repository.createEmptyPlayer(teamId.toInt())
                        }
                    }

                    repository.finalizeTournament(
                        tournamentObject,
                        adminState.value.createTournamentMatches,
                        adminState.value.createTournamentTeams,
                        adminState.value.createTournamentPlayers
                    )
                }

                _adminState.update { it.copy(
                    createTournamentName = "",
                    createTournamentMatches = emptyList(),
                    createTournamentTeams = emptyList(),
                    createTournamentPlayers = emptyList()
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
                    TeamDetails(teamName = "Team A"),
                    TeamDetails(teamName = "Team B"),
                    TeamDetails(teamName = "Team C"),
                    TeamDetails(teamName = "Team D")
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
            is AdminEvent.UpdatePlayer -> {
                _adminState.update {
                    it.copy(
                        createTournamentPlayers = _adminState.value.createTournamentPlayers.map { if (it == event.player) event.player else it }
                    )
                }
            }
            is AdminEvent.AddPlayer -> {
                _adminState.update {
                    it.copy(
                        createTournamentPlayers = _adminState.value.createTournamentPlayers + event.player
                    )
                }
            }
        }
    }
}