package com.example.ubkk3.match

import kotlinx.serialization.Serializable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Serializable
data class Tournament(
    val id: String = "",
    val tournamentName: String = "",
    val matches: List<MatchDetails> = emptyList(),
    val isActive: Boolean = false
)
