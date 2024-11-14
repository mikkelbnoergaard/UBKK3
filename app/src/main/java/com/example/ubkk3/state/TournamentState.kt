package com.example.ubkk3.state

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.Tournament

data class TournamentState(

    val activeTournaments: List<Tournament> = emptyList(),
    val selectedTournament: Tournament? = null,
    val matchDetails: MatchDetails? = null,
    val zoomLevel: Float = 0.2f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,

    val isViewingMatch: Boolean = false,

    val creatingTournamentMembers: List<Player> = emptyList(),

    )
