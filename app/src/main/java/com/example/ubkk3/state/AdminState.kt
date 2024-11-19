package com.example.ubkk3.state

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

data class AdminState(
    val tournaments: List<Tournament> = emptyList(),
    val selectedTournament: Tournament? = null,

    //for creating tournaments
    val createTournamentTeams: List<TeamDetails> = emptyList(),
    val createTournamentName: String = "",
    val createTournamentMatches: List<MatchDetails> = emptyList()
)
