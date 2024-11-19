package com.example.ubkk3.ui.event

import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

sealed interface TournamentEvent {

    object FetchTournaments: TournamentEvent

    data class UpdateMatchWinner(val tournamentId: Int, val matchId: Int, val winningTeam: Int): TournamentEvent
    data class UpdateMatchDetails(val matchId: Int): TournamentEvent
    data class SetSelectedTournament(val tournament: Tournament): TournamentEvent

    data class UpdateZoomLevel(val zoomLevel: Float): TournamentEvent
    data class UpdateOffsetX(val offsetX: Float): TournamentEvent
    data class UpdateOffsetY(val offsetY: Float): TournamentEvent



}