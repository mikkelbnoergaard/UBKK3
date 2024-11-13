package com.example.ubkk3.ui.admin

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val repository = FirebaseRepository(context)

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    init {
        loadTournaments()
    }

    private fun loadTournaments() {
        viewModelScope.launch {
            val tournaments = repository.loadAllTournamentsTitles()
            _tournaments.value = tournaments
        }
        println("Tournaments loaded: " + _tournaments.value)
    }

    fun updateTournamentStatus(tournamentName: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.updateTournamentStatus(tournamentName, isActive)
            loadTournaments()
        }
    }
}