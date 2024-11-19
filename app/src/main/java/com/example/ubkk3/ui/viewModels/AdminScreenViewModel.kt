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
                    matches = adminState.value.createTournamentMatches,
                    isActive = true,
                )

                if(tournamentObject.tournamentName.isEmpty()){
                    println("Tournament name is empty")
                    return
                }

                viewModelScope.launch {
                    repository.createTournament(tournamentObject)
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
                    TeamDetails(teamName = "Team A", player1 = Player(name = "Player 1A", email = "Player@1A"), player2 = Player(name = "Player 2A", email = "Player@2A")),
                    TeamDetails(teamName = "Team B", player1 = Player(name = "Player 1B", email = "Player@1B"), player2 = Player(name = "Player 2B", email = "Player@2B")),
                    TeamDetails(teamName = "Team C", player1 = Player(name = "Player 1C", email = "Player@1C"), player2 = Player(name = "Player 2C", email = "Player@2C")),
                    TeamDetails(teamName = "Team D", player1 = Player(name = "Player 1D", email = "Player@1D"), player2 = Player(name = "Player 2D", email = "Player@2D"))
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