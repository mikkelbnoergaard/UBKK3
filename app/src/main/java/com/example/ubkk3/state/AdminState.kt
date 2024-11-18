package com.example.ubkk3.state

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

data class AdminState(
    val tournaments: List<Tournament> = emptyList(),
    val teams: List<TeamDetails> = emptyList(),
)
