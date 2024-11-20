package com.example.ubkk3.ui.event

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

sealed interface AdminEvent {

    object GenerateMatches: AdminEvent
    object AddTestTeams: AdminEvent

    data class AddPlayer(val player: Player): AdminEvent
    data class UpdatePlayer(val player: Player): AdminEvent
    data class SaveTournamentInDatabase(val tournament: Tournament, val matches: List<MatchDetails>, val teams: List<TeamDetails>, val players: List<Player>): AdminEvent
    data class UpdateTournamentActivityStatus(val tournament: Tournament): AdminEvent
    data class AddTeam(val team: TeamDetails): AdminEvent
    data class UpdateTeam(val updatedTeam: TeamDetails): AdminEvent
    data class DeleteTeam(val team: TeamDetails): AdminEvent
    data class SetTournamentTitle(val title: String): AdminEvent

}