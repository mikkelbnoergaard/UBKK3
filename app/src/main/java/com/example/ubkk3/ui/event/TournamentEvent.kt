package com.example.ubkk3.ui.event

sealed interface TournamentEvent {

    object FetchTournaments: TournamentEvent

    data class UpdateMatchWinner(val tournamentId: String, val matchId: String, val winnerId: Int): TournamentEvent
    data class SelectTournament(val tournamentId: String): TournamentEvent

    data class UpdateZoomLevel(val zoomLevel: Float): TournamentEvent
    data class UpdateOffsetX(val offsetX: Float): TournamentEvent
    data class UpdateOffsetY(val offsetY: Float): TournamentEvent



}