package com.example.ubkk3.ui.tournament

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.repository.FirebaseRepository
import com.example.ubkk3.state.TournamentState
import com.example.ubkk3.ui.event.TournamentEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class TournamentScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private val repository = FirebaseRepository(context)

    private val _activeTournaments = MutableStateFlow<List<Tournament>>(emptyList())
    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    private val _matchDetails = MutableStateFlow<MatchDetails?>(null)

    private val _tournamentState = MutableStateFlow(TournamentState())
    val tournamentState = combine(_tournamentState, _activeTournaments, _selectedTournament, _matchDetails) { tournamentState, activeTournaments, selectedTournament, matchDetails ->
        tournamentState.copy(
            activeTournaments = activeTournaments,
            selectedTournament = selectedTournament,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TournamentState())

    init {
        fetchActiveTournaments()
    }

    fun onEvent(event: TournamentEvent) {
        when (event) {

            is TournamentEvent.SelectTournament -> {
                viewModelScope.launch {
                    _selectedTournament.value = event.tournament
                }
            }

            is TournamentEvent.UpdateMatchWinner -> {
                viewModelScope.launch {
                    repository.updateMatchResult(event.tournamentId, event.matchId, event.winnerId)
                    // Fetch the updated tournament details
                    val updatedTournament = repository.loadTournamentById(event.tournamentId)
                    _selectedTournament.value = updatedTournament
                    _tournamentState.update {
                        it.copy(
                            matchDetails = repository.loadMatchDetails(tournamentState.value.selectedTournament!!.id, event.matchId)
                        )
                    }
                }
            }

            is TournamentEvent.FetchTournaments -> {
                fetchActiveTournaments()
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

            is TournamentEvent.UpdateMatchDetails -> {
                viewModelScope.launch {
                    _tournamentState.update {
                        it.copy(
                            matchDetails = repository.loadMatchDetails(tournamentState.value.selectedTournament!!.id, event.matchId)
                        )
                    }
                }
            }
        }
    }

    private fun fetchActiveTournaments() {
        viewModelScope.launch {
            val tournaments = repository.loadAllTournamentsWithMatches().filter { it.isActive }
            _activeTournaments.value = tournaments
            if (_selectedTournament.value == null && tournaments.isNotEmpty()) {
                _selectedTournament.value = tournaments[0]
            }
        }
    }

}