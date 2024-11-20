package com.example.ubkk3.state

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

data class CombinedState(
    val activeTournaments: List<Tournament>,
    val selectedTournament: Tournament?,
    val selectedTournamentMatches: List<MatchDetails>,
    val selectedTournamentTeams: Map<Int, List<TeamDetails>>,
    val selectedTournamentPlayers: Map<Int, List<Player>>
)
