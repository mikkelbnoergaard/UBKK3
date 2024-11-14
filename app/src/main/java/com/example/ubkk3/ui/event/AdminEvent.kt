package com.example.ubkk3.ui.event

import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

sealed interface AdminEvent {

    object LoadTournaments: AdminEvent
    object GenerateMatches: AdminEvent
    object SaveTournamentToFirebase: AdminEvent
    object AddTestTeams: AdminEvent

    data class UpdateTournamentStatus(val tournamentName: String, val isActive: Boolean): AdminEvent
    data class AddTeam(val team: TeamDetails): AdminEvent
    data class UpdateTeam(val updatedTeam: TeamDetails): AdminEvent
    data class DeleteTeam(val team: TeamDetails): AdminEvent
    data class SetTournamentTitle(val title: String): AdminEvent

}