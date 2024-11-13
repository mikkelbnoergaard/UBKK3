package com.example.ubkk3.match

import kotlinx.serialization.Serializable

@Serializable
data class MatchDetails(
    val id: String = "",

    val team1: TeamDetails? = null,
    val team1Won: Boolean? = false,

    val team2: TeamDetails? = null,
    val team2Won: Boolean? = false,

    val round: String? = null
)