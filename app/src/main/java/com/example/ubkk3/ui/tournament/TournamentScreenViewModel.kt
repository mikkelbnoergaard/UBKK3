package com.example.ubkk3.ui.tournament

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.repository.FirebaseRepository
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
    val activeTournaments = _activeTournaments.asStateFlow()

    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    val selectedTournament: StateFlow<Tournament?> = _selectedTournament.asStateFlow()

    /*
    val selectedTournament = activeTournaments.map { activeTournaments ->
        activeTournaments.find { it.id == (theSelectedTournament.value?.id ?: "") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

     */

    private val _matchDetails = MutableStateFlow<MatchDetails?>(null)
    val matchDetails: StateFlow<MatchDetails?> = _matchDetails.asStateFlow()

    private val _zoomLevel = MutableStateFlow(0.2f)
    val zoomLevel: StateFlow<Float> = _zoomLevel.asStateFlow()

    private val _offsetX = MutableStateFlow(0f)
    val offsetX: StateFlow<Float> = _offsetX.asStateFlow()

    private val _offsetY = MutableStateFlow(0f)
    val offsetY: StateFlow<Float> = _offsetY.asStateFlow()


    private val _tournamentState = MutableStateFlow(TournamentState())
    val tournamentState = combine(_tournamentState, _activeTournaments, _selectedTournament, _matchDetails) {tournamentState, activeTournaments, selectedTournament, matchDetails ->
        tournamentState.copy(
            activeTournaments = activeTournaments,
            selectedTournament = selectedTournament,

        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TournamentState())

    init {
        fetchActiveTournaments()
    }

    fun loadMatchDetails(matchDetails: MatchDetails) {
        _matchDetails.value = matchDetails
    }

    fun updateMatchWinner(tournamentId: String, matchId: String, whichTeamWon: Int) {
        viewModelScope.launch {
            repository.updateMatchResult(tournamentId, matchId, whichTeamWon)
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

    fun refreshActiveTournaments() {
        viewModelScope.launch {
            val tournaments = repository.loadAllTournamentsWithMatches().filter { it.isActive }
            _activeTournaments.value = tournaments
        }
    }

    fun selectTournament(tournament: Tournament) {
        _tournamentState.update {
            it.copy(selectedTournament = tournament)
        }
    }

    fun updateZoomLevel(newZoomLevel: Float) {
        _zoomLevel.value = newZoomLevel
    }

    fun updateOffsetX(newOffsetX: Float) {
        _offsetX.value = newOffsetX
    }

    fun updateOffsetY(newOffsetY: Float) {
        _offsetY.value = newOffsetY
    }
}

data class TournamentState(
    val activeTournaments: List<Tournament> = emptyList(),
    val selectedTournament: Tournament? = null,
    val matchDetails: MatchDetails? = null,
    val zoomLevel: Float = 0.2f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,

)