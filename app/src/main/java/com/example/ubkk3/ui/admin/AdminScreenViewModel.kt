package com.example.ubkk3.ui.admin

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.generateTournament.generateTournament
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.repository.FirebaseRepository
import com.example.ubkk3.state.AdminState
import com.example.ubkk3.state.CreateTournamentState
import com.example.ubkk3.ui.event.AdminEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger


class AdminScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val repository = FirebaseRepository(context)

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    private val _createTournamentPlayers = MutableStateFlow<List<Player>>(emptyList())
    private val _teams = MutableStateFlow(repository.loadTeams())
    private val _createTournamentTitle = MutableStateFlow("")
    private val _createTournamentMatches = MutableStateFlow<List<MatchDetails>>(emptyList())
    private val idCounter = AtomicInteger(_teams.value.size)


    private val _adminState = MutableStateFlow(AdminState())
    val adminState = combine(_adminState, _tournaments, _teams) { adminState, tournaments, teams ->
        adminState.copy(
            tournaments = tournaments,
            teams = teams,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminState())

    private val _createTournamentState = MutableStateFlow(CreateTournamentState())
    val createTournamentState = combine(_createTournamentState, _createTournamentTitle, _createTournamentMatches, _createTournamentPlayers) { createTournamentState, createTournamentTitle, createTournamentMatches, createTournamentPlayers ->
        createTournamentState.copy(
            createTournamentTitle = createTournamentTitle,
            createTournamentMatches = createTournamentMatches,
            createTournamentPlayers = createTournamentPlayers,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateTournamentState())

    init {
        loadTournaments()
    }

    fun onEvent(event: AdminEvent) {
        when(event) {
            is AdminEvent.LoadTournaments -> {
                loadTournaments()
            }
            is AdminEvent.UpdateTournamentStatus -> {
                updateTournamentStatus(event.tournamentName, event.isActive)
            }
            is AdminEvent.AddTeam -> {
                addTeam(event.team)
            }
            is AdminEvent.UpdateTeam -> {
                updateTeam(event.updatedTeam)
            }
            is AdminEvent.DeleteTeam -> {
                deleteTeam(event.team)
            }
            is AdminEvent.SetTournamentTitle -> {
                _createTournamentTitle.value = event.title
                _createTournamentState.update { currentState ->
                    currentState.copy(
                        createTournamentTitle = event.title
                    )
                }
            }
            is AdminEvent.GenerateMatches -> {
                _createTournamentMatches.value = generateTournament(_teams.value)
                _createTournamentState.update {
                    it.copy(
                        createTournamentMatches = generateTournament(_teams.value)
                    )
                }
            }
            is AdminEvent.SaveTournamentToFirebase -> {
                val tournament = Tournament(
                    tournamentName = createTournamentState.value.createTournamentTitle,
                    matches = _createTournamentState.value.createTournamentMatches,
                    isActive = false
                )
                viewModelScope.launch {
                    repository.saveTournamentToFirebase(tournament)
                }
                _createTournamentState.update {
                    it.copy(
                        createTournamentTitle = "",
                        createTournamentMatches = emptyList(),
                        createTournamentPlayers = emptyList(),
                    )
                }
            }
            is AdminEvent.AddTestTeams -> {
                addTestTeams()
            }
        }
    }


    private fun loadTournaments() {
        viewModelScope.launch {
            val tournaments = repository.loadAllTournamentsTitles()
            _tournaments.value = tournaments
        }
        println("Tournaments loaded: " + _tournaments.value)
    }

    private fun updateTournamentStatus(tournamentName: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.updateTournamentStatus(tournamentName, isActive)
            loadTournaments()
        }
    }

    private fun addTeam(team: TeamDetails) {
        val newTeam = team.copy(id = (idCounter.incrementAndGet()).toString())
        _teams.value = _teams.value + newTeam
        repository.saveTeams(_teams.value)
    }

    private fun updateTeam(updatedTeam: TeamDetails) {
        _teams.value = _teams.value.map { if (it.id == updatedTeam.id) updatedTeam else it }
        repository.saveTeams(_teams.value)
    }

    private fun deleteTeam(team: TeamDetails) {
        _teams.value = _teams.value.filter { it != team }
        shiftTeamIds()
        repository.saveTeams(_teams.value)
    }

    private fun shiftTeamIds() {
        _teams.value = _teams.value.mapIndexed { index, team ->
            team.copy(id = (index + 1).toString())
        }
        idCounter.set(_teams.value.size)
    }

    private fun addTestTeams() {
        val testTeams = listOf(
            TeamDetails(id = (idCounter.incrementAndGet()).toString(), teamName = "Team A", member1 = Player("Player 1A"), member2 = Player("Player 2A")),
            TeamDetails(id = (idCounter.incrementAndGet()).toString(), teamName = "Team B", member1 = Player("Player 1B"), member2 = Player("Player 2B")),
            TeamDetails(id = (idCounter.incrementAndGet()).toString(), teamName = "Team C", member1 = Player("Player 1C"), member2 = Player("Player 2C")),
            TeamDetails(id = (idCounter.incrementAndGet()).toString(), teamName = "Team D", member1 = Player("Player 1D"), member2 = Player("Player 2D"))
        )
        _teams.value = _teams.value + testTeams
        repository.saveTeams(_teams.value)
    }


}