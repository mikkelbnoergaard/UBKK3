package com.example.ubkk3.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.ubkk3.dataLayer.local.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.generateTournament.generateTournament
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.state.TournamentState
import com.example.ubkk3.ui.event.TournamentEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TournamentScreenViewModel(
    private val repository: TournamentRepository
): ViewModel() {

    private val _activeTournaments = MutableStateFlow(repository.observeAll())
    private val _selectedTournament = MutableStateFlow(repository.getTournamentById(0))

    private val _tournamentState = MutableStateFlow(TournamentState())
    val tournamentState = combine(_tournamentState, _activeTournaments, _selectedTournament) { tournamentState, activeTournaments, selectedTournament ->
        tournamentState.copy(
            activeTournaments = activeTournaments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()).value,
            selectedTournament = selectedTournament
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TournamentState())

    fun onEvent(event: TournamentEvent) {
        when (event) {
            is TournamentEvent.UpdateMatchWinner -> {
                viewModelScope.launch {
                    repository.updateMatchWinner(event.tournamentId, event.matchId, event.winningTeam)
                }
            }

            is TournamentEvent.SetSelectedTournament -> {
                viewModelScope.launch {
                    _selectedTournament.value = repository.getTournamentById(event.tournament.id)
                }
            }

            is TournamentEvent.UpdateMatchDetails -> {
                viewModelScope.launch {
                    // TO DO
                }
            }

            is TournamentEvent.UpdateZoomLevel -> {
                _tournamentState.update {it.copy(zoomLevel = event.zoomLevel) }
            }

            is TournamentEvent.UpdateOffsetX -> {
                _tournamentState.update {it.copy(offsetX = event.offsetX) }
            }

            is TournamentEvent.UpdateOffsetY -> {
                _tournamentState.update {it.copy(offsetY = event.offsetY) }
            }

            is TournamentEvent.FetchTournaments -> {
                viewModelScope.launch {
                    _tournamentState.update { it.copy(
                        activeTournaments = repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()).value
                    )}
                }
            }
        }
    }

}