package com.example.ubkk3.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.ubkk3.dataLayer.local.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.generateTournament.generateTournament
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.state.CombinedState
import com.example.ubkk3.state.TournamentState
import com.example.ubkk3.ui.event.TournamentEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TournamentScreenViewModel(
    private val repository: TournamentRepository
): ViewModel() {

    private val _activeTournaments = MutableStateFlow<List<Tournament>>(emptyList())
    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    private val _matches = MutableStateFlow<List<MatchDetails>>(emptyList())
    private val _teams = MutableStateFlow<Map<Int, List<TeamDetails>>>(emptyMap())
    private val _players = MutableStateFlow<Map<Int, List<Player>>>(emptyMap())

    private val _tournamentState = MutableStateFlow(TournamentState())
    val tournamentState = combine(
        _activeTournaments,
        _selectedTournament,
        _matches,
        _teams,
        _players
    ) { activeTournaments, selectedTournament, matches, teams, players ->
        CombinedState(
            activeTournaments = activeTournaments,
            selectedTournament = selectedTournament,
            selectedTournamentMatches = matches,
            selectedTournamentTeams = teams,
            selectedTournamentPlayers = players
        )
    }.map { combinedState ->
        _tournamentState.value.copy(
            activeTournaments = combinedState.activeTournaments,
            selectedTournament = combinedState.selectedTournament,
            selectedTournamentMatches = combinedState.selectedTournamentMatches,
            selectedTournamentTeams = combinedState.selectedTournamentTeams,
            selectedTournamentPlayers = combinedState.selectedTournamentPlayers
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TournamentState())

    init {
        viewModelScope.launch {
            _activeTournaments.value = repository.observeAll().first()
        }
    }

    fun onEvent(event: TournamentEvent) {
        when (event) {
            is TournamentEvent.UpdateMatchWinner -> {
                viewModelScope.launch {
                    repository.updateMatchWinner(event.tournamentId, event.matchId, event.winningTeam)
                }
            }

            is TournamentEvent.SetSelectedTournament -> {
                viewModelScope.launch {
                    _selectedTournament.value = repository.getTournamentById(event.tournament.id).first()
                    _matches.value = repository.getMatchesByTournamentId(event.tournament.id)
                    _matches.value.forEach { match ->
                        _teams.value = _teams.value + (match.id to repository.getTeamsByMatchId(match.id))
                        _teams.value[match.id]?.forEach { team ->
                            _players.value = _players.value + (team.id to repository.getPlayersByTeamId(team.id))
                        }
                    }
                }
            }

            is TournamentEvent.UpdateMatchDetails -> {
                viewModelScope.launch {
                    // TO DO
                }
            }

            is TournamentEvent.UpdateZoomLevel -> {
                _tournamentState.update { it.copy(zoomLevel = event.zoomLevel) }
            }

            is TournamentEvent.UpdateOffsetX -> {
                _tournamentState.update { it.copy(offsetX = event.offsetX) }
            }

            is TournamentEvent.UpdateOffsetY -> {
                _tournamentState.update { it.copy(offsetY = event.offsetY) }
            }

            is TournamentEvent.FetchTournaments -> {
                viewModelScope.launch {
                    _activeTournaments.value = repository.observeAll().first()
                }
            }
        }
    }
}