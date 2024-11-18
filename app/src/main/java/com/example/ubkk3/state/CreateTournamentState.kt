package com.example.ubkk3.state

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player

data class CreateTournamentState(
    val createTournamentTitle: String = "",
    val createTournamentMatches: List<MatchDetails> = emptyList(),
    val createTournamentPlayers: List<Player> = emptyList(),
)
