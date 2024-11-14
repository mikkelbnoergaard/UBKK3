package com.example.ubkk3.ui.admin

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.generateTournament.generateTournament
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.repository.FirebaseRepository
import com.example.ubkk3.state.AdminState
import com.example.ubkk3.ui.event.AdminEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger


class AdminScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val repository = FirebaseRepository(context)

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    private val _creatingTournamentPlayers = MutableStateFlow<List<Player>>(emptyList())
    private val _teams = MutableStateFlow(repository.loadTeams())
    private val _tournamentTitle = MutableStateFlow("")
    private val idCounter = AtomicInteger(_teams.value.size)

    private val _adminState = MutableStateFlow(AdminState())
    val adminState = combine(_adminState, _tournaments, _creatingTournamentPlayers, _teams, _tournamentTitle) { adminState, tournaments, creatingTournamentPlayers, teams, tournamentTitle ->
        adminState.copy(
            tournaments = tournaments,
            creatingTournamentPlayers = creatingTournamentPlayers,
            teams = teams,
            createTournamentTitle = tournamentTitle
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminState())

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
                println("Tournament title before: " + adminState.value.createTournamentTitle)
                _adminState.update {
                    it.copy(
                        createTournamentTitle = event.title
                    )
                }
                println("Tournament title set: " + event.title)
            }
            is AdminEvent.GenerateMatches -> {
                _adminState.update {
                    it.copy(
                        matches = generateTournament(_teams.value)
                    )
                }
            }
            is AdminEvent.SaveTournamentToFirebase -> {
                val tournament = Tournament(
                    id = adminState.value.createTournamentTitle,
                    tournamentName = adminState.value.createTournamentTitle,
                    matches = adminState.value.matches,
                    isActive = false
                )
                viewModelScope.launch {
                    repository.saveTournamentToFirebase(tournament)
                    _tournamentTitle.value = ""
                }
                _adminState.update {
                    it.copy(
                        createTournamentTitle = "",
                        matches = emptyList(),
                        creatingTournamentPlayers = emptyList(),
                        teams = emptyList(),
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