package com.example.ubkk3.generateTournament

import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import kotlin.random.Random

/*
fun generateTournament(teams: List<Team>): List<Match> {
    val matches = mutableListOf<Match>()
    val groups = teams.chunked(4).filter { it.size == 4 }
    val groupWinners = mutableListOf<Team>()
    val losersBracketTeams = mutableListOf<Team>()

    // Group stages
    groups.forEachIndexed { index, group ->
        for (i in group.indices) {
            for (j in i + 1 until group.size) {
                matches.add(Match(group[i], group[j], "Group ${index + 1}", Pair(index, matches.size)))
            }
        }
        //groupWinners.addAll(group.take(2)) // Top 2 teams advance
        //losersBracketTeams.addAll(group.drop(2)) // Bottom 2 teams to losers bracket
    }

    /*
    // Losers bracket
    val losersBracketMatches = losersBracketTeams.chunked(4).flatMapIndexed { index, group ->
        group.flatMap { team1 ->
            group.filter { it != team1 }.map { team2 ->
                Match(team1, team2, "Losers Bracket", Pair(index, matches.size))
            }
        }
    }
    matches.addAll(losersBracketMatches)

     */

    /*
    // Knockout stage
    val knockoutTeams = groupWinners + losersBracketTeams.chunked(4).mapNotNull { it.firstOrNull() }
    var round = 1
    var currentRoundTeams = knockoutTeams
    while (currentRoundTeams.size > 1) {
        val nextRoundTeams = mutableListOf<Team>()
        currentRoundTeams.chunked(2).forEachIndexed { index, pair ->
            val match = Match(pair.getOrNull(0), pair.getOrNull(1), "Knockout Round $round", Pair(round, index))
            matches.add(match)
            pair.getOrNull(0)?.let { nextRoundTeams.add(it) }
        }
        currentRoundTeams = nextRoundTeams
        round++
    }

     */

    /*
    // Bronze and gold matches
    matches.add(Match(null, null, "Bronze Match", Pair(round, 0)))
    matches.add(Match(null, null, "Gold Match", Pair(round, 1)))

     */

    return matches
}



fun generateRandomTeams(): List<Team> {
    val teamCount = (Random.nextInt(2, 9) * 4) // Generates a number between 8 and 32 that is divisible by 4
    return List(teamCount) { index ->
        Team(name = "Team ${index + 1}", members = listOf("Member 1", "Member 2"))
    }
}

 */


fun generateTournament(teams: List<TeamDetails>): List<MatchDetails> {
    val matches = mutableListOf<MatchDetails>()
    val groupSize = 4
    val groups = if (teams.size >= groupSize) teams.chunked(groupSize) else listOf(teams)

    // Group stages
    groups.forEachIndexed { index, group ->
        for (i in group.indices) {
            for (j in i + 1 until group.size) {
                matches.add(
                    MatchDetails(
                        team1 = group[i],
                        team2 = group[j],
                        round = "Group ${index + 1}",
                        team1Won = false,
                        team2Won = false,
                    )
                )
            }
        }
    }

    return matches
}

fun generateRandomTeams(): List<TeamDetails> {
    val teamCount = (Random.nextInt(2, 9) * 4) // Generates a number between 8 and 32 that is divisible by 4
    return List(teamCount) { index ->
        TeamDetails(
            teamName = "Team ${index + 1}",
            player1 = Player(name = "Member 1", email = "Email1", cupsHit = 0, redemptions = 0, trickshots = 0),
            player2 = Player(name = "Member 2", email = "Email2", cupsHit = 0, redemptions = 0, trickshots = 0)
        )
    }
}
