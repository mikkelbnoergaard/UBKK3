package com.example.ubkk3.ui.event

import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

sealed interface AdminEvent {

    object SaveTournamentInDatabase: AdminEvent
    object GenerateMatches: AdminEvent
    object AddTestTeams: AdminEvent

    data class UpdateTournamentActivityStatus(val tournament: Tournament): AdminEvent
    data class AddTeam(val team: TeamDetails): AdminEvent
    data class UpdateTeam(val updatedTeam: TeamDetails): AdminEvent
    data class DeleteTeam(val team: TeamDetails): AdminEvent
    data class SetTournamentTitle(val title: String): AdminEvent

}