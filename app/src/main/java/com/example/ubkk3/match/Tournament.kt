package com.example.ubkk3.match

import kotlinx.serialization.Serializable

@Serializable
data class Tournament(
    val id: String = "",
    val tournamentName: String = "",
    val matches: List<MatchDetails> = mutableListOf(),
    val isActive: Boolean = false
)
