package com.example.ubkk3.ui.event

import com.example.ubkk3.match.Tournament

sealed interface TournamentEvent {

    object FetchTournaments: TournamentEvent

    data class UpdateMatchWinner(val tournamentId: String, val matchId: String, val winnerId: Int): TournamentEvent
    data class SelectTournament(val tournament: Tournament): TournamentEvent
    data class UpdateMatchDetails(val matchId: String): TournamentEvent

    data class UpdateZoomLevel(val zoomLevel: Float): TournamentEvent
    data class UpdateOffsetX(val offsetX: Float): TournamentEvent
    data class UpdateOffsetY(val offsetY: Float): TournamentEvent



}