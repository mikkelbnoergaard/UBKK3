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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.atomic.AtomicInteger

class CreateTournamentViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val repository = FirebaseRepository(context)

    private val _teams = MutableStateFlow<List<TeamDetails>>(repository.loadTeams())
    val teams: StateFlow<List<TeamDetails>> = _teams

    private val _matches = MutableStateFlow<List<MatchDetails>>(emptyList())
    val matches: StateFlow<List<MatchDetails>> = _matches.asStateFlow()

    private val _tournamentTitle = MutableStateFlow("")
    val tournamentTitle: StateFlow<String> = _tournamentTitle.asStateFlow()

    private val idCounter = AtomicInteger(_teams.value.size)

    init {
        loadMatchesFromTournament("tournament1")
    }

    fun addTeam(team: TeamDetails) {
        val newTeam = team.copy(id = (idCounter.incrementAndGet()).toString())
        _teams.value = _teams.value + newTeam
        repository.saveTeams(_teams.value)
    }

    fun updateTeam(updatedTeam: TeamDetails) {
        _teams.value = _teams.value.map { if (it.id == updatedTeam.id) updatedTeam else it }
        repository.saveTeams(_teams.value)
    }

    fun deleteTeam(team: TeamDetails) {
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

    fun generateMatches() {
        _matches.value = generateTournament(_teams.value)
    }

    fun loadMatchesFromTournament(title: String) {
        viewModelScope.launch {
            _matches.value = repository.loadMatchesFromTournament(title)
        }
    }

    fun setTournamentTitle(title: String) {
        _tournamentTitle.value = title
    }

    fun saveTournamentToFirebase(title: String) {
        val tournament = Tournament(
            id = title,
            tournamentName = title,
            matches = _matches.value,
            isActive = true // or set based on your logic
        )
        repository.saveTournamentToFirebase(tournament)
        _tournamentTitle.value = ""
    }

    fun clearData() {
        _teams.value = emptyList()
        _matches.value = emptyList()
        _tournamentTitle.value = ""
    }

    fun addTestTeams() {
        val testTeams = (1..8).map { i ->
            TeamDetails(
                id = i.toString(),
                teamName = "Team $i",
                member1 = Player(name = "Member ${i}1", email = "Email ${i}1", 0, 0, 0),
                member2 = Player(name = "Member ${i}2", email = "Email ${i}2", 0, 0, 0),
            )
        }
        _teams.value = _teams.value + testTeams
        repository.saveTeams(_teams.value)
    }
}