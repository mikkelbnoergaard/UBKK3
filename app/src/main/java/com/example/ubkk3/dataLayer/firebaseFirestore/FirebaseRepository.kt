package com.example.ubkk3.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.match.Player
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*
class FirebaseRepository(private val context: Context) {
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val sharedPreferences = context.getSharedPreferences("tournament_prefs", Context.MODE_PRIVATE)

    suspend fun loadMatchesFromTournament(title: String): List<MatchDetails> {
        return try {
            val groupStagesRef = database.collection("tournaments").document(title).collection("group stages")
            val losersBracketRef = database.collection("tournaments").document(title).collection("losers_bracket")

            val groupStagesQuerySnapshot = groupStagesRef.get().await()
            val losersBracketQuerySnapshot = losersBracketRef.get().await()

            val groupStagesMatches = groupStagesQuerySnapshot.documents.mapNotNull { document ->
                MatchDetails(
                    id = document.id,
                    team1 = document.get("team1")?.let { team ->
                        val teamMap = team as Map<*, *>
                        TeamDetails(
                            id = teamMap["id"] as? String ?: "",
                            teamName = teamMap["teamName"] as? String ?: "",
                            member1 = teamMap["member1"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            },
                            member2 = teamMap["member2"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            }
                        )
                    },
                    team1Won = document.getBoolean("team1Won") ?: false,
                    team2 = document.get("team2")?.let { team ->
                        val teamMap = team as Map<*, *>
                        TeamDetails(
                            id = teamMap["id"] as? String ?: "",
                            teamName = teamMap["teamName"] as? String ?: "",
                            member1 = teamMap["member1"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            },
                            member2 = teamMap["member2"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            }
                        )
                    },
                    team2Won = document.getBoolean("team2Won") ?: false,
                    round = document.getString("round") ?: ""
                )
            }

            val losersBracketMatches = losersBracketQuerySnapshot.documents.mapNotNull { document ->
                MatchDetails(
                    id = document.id,
                    team1 = document.get("team1")?.let { team ->
                        val teamMap = team as Map<*, *>
                        TeamDetails(
                            id = teamMap["id"] as? String ?: "",
                            teamName = teamMap["teamName"] as? String ?: "",
                            member1 = teamMap["member1"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            },
                            member2 = teamMap["member2"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            }
                        )
                    },
                    team1Won = document.getBoolean("team1Won") ?: false,
                    team2 = document.get("team2")?.let { team ->
                        val teamMap = team as Map<*, *>
                        TeamDetails(
                            id = teamMap["id"] as? String ?: "",
                            teamName = teamMap["teamName"] as? String ?: "",
                            member1 = teamMap["member1"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            },
                            member2 = teamMap["member2"]?.let { member ->
                                val memberMap = member as Map<*, *>
                                Player(
                                    name = memberMap["name"] as? String ?: "",
                                    cupsHit = (memberMap["cupsHit"] as? Long)?.toInt() ?: 0,
                                    redemptions = (memberMap["redemptions"] as? Long)?.toInt() ?: 0,
                                    trickshots = (memberMap["trickshots"] as? Long)?.toInt() ?: 0
                                )
                            }
                        )
                    },
                    team2Won = document.getBoolean("team2Won") ?: false,
                    round = document.getString("round") ?: ""
                )
            }

            groupStagesMatches + losersBracketMatches
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                Log.d("Error loading matches", e.message.toString())
            }
            emptyList()
        }
    }

    fun saveTournamentToFirebase(tournament: Tournament) {
        println("THIS WAS CALLED!!")
        println("tournament name: ${tournament.tournamentName}")
        // Generate a unique document ID
        val documentId = database.collection("tournaments").document().id

        // Create a valid document reference
        val documentReference = database.collection("tournaments").document(documentId)

        val tournamentData = mapOf(
            "id" to tournament.id,
            "tournamentName" to tournament.tournamentName,
            "isActive" to tournament.isActive
        )

        // Save the tournament to Firestore
        documentReference.set(tournamentData)
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Tournament details saved successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepository", "Error saving tournament details", exception)
            }

        val matchesCollection = documentReference.collection("group stages")
        tournament.matches.forEachIndexed { index, match ->
            val matchData = mapOf(
                "id" to match.id,
                "team1" to match.team1?.let { team ->
                    mapOf(
                        "id" to team.id,
                        "teamName" to team.teamName,
                        "member1" to team.member1?.let { member ->
                            mapOf(
                                "name" to member.name,
                                "cupsHit" to member.cupsHit,
                                "redemptions" to member.redemptions,
                                "trickshots" to member.trickshots
                            )
                        },
                        "member2" to team.member2?.let { member ->
                            mapOf(
                                "name" to member.name,
                                "cupsHit" to member.cupsHit,
                                "redemptions" to member.redemptions,
                                "trickshots" to member.trickshots
                            )
                        }
                    )
                },
                "team1Won" to match.team1Won,
                "team2" to match.team2?.let { team ->
                    mapOf(
                        "id" to team.id,
                        "teamName" to team.teamName,
                        "member1" to team.member1?.let { member ->
                            mapOf(
                                "name" to member.name,
                                "cupsHit" to member.cupsHit,
                                "redemptions" to member.redemptions,
                                "trickshots" to member.trickshots
                            )
                        },
                        "member2" to team.member2?.let { member ->
                            mapOf(
                                "name" to member.name,
                                "cupsHit" to member.cupsHit,
                                "redemptions" to member.redemptions,
                                "trickshots" to member.trickshots
                            )
                        }
                    )
                },
                "team2Won" to match.team2Won,
                "round" to match.round
            )
            matchesCollection.document(index.toString()).set(matchData)
                .addOnSuccessListener {
                    Log.d("FirebaseRepository", "Match $index saved successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseRepository", "Error saving match $index", exception)
                }
        }
    }


    fun saveTeams(teams: List<TeamDetails>) {
        val teamsJson = Json.encodeToString(teams)
        sharedPreferences.edit().putString("teams", teamsJson).apply()
    }

    fun loadTeams(): List<TeamDetails> {
        val teamsJson = sharedPreferences.getString("teams", null) ?: return emptyList()
        return Json.decodeFromString(teamsJson)
    }

    suspend fun loadAllTournamentsTitles(): List<Tournament> {
        return try {
            val tournamentsRef = database.collection("tournaments")
            val querySnapshot = tournamentsRef.get().await()
            querySnapshot.documents.mapNotNull { document ->
                Tournament(
                    id = document.id,
                    tournamentName = document.getString("tournamentName") ?: "",
                    isActive = document.getBoolean("isActive") ?: false
                )
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                Log.d("Error loading tournaments", e.message.toString())
            }
            emptyList()
        }
    }

    suspend fun loadAllTournamentsWithMatches(): List<Tournament> {
        return try {
            val tournamentsRef = database.collection("tournaments")
            val querySnapshot = tournamentsRef.get().await()
            val tournaments = querySnapshot.documents.mapNotNull { document ->
                Tournament(
                    id = document.id,
                    tournamentName = document.getString("tournamentName") ?: "",
                    isActive = document.getBoolean("isActive") ?: false,
                    matches = loadMatchesFromTournament(document.id)
                )
            }
            tournaments
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                Log.d("Error loading tournaments with matches", e.message.toString())
            }
            emptyList()
        }
    }

    suspend fun updateTournamentStatus(tournamentName: String, isActive: Boolean) {
        val tournamentDoc = database.collection("tournaments").document(tournamentName)
        try {
            tournamentDoc.update("isActive", isActive).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating tournament status", e)
        }
    }

    suspend fun updateMatchResult(tournamentId: String, matchId: String, whichTeamWon: Int) {
        val matchDoc = database.collection("tournaments")
            .document(tournamentId)
            .collection("group stages")
            .document(matchId)

        try {
            Log.d("FirebaseRepository", "Updating match result for matchId: $matchId in tournamentId: $tournamentId with whichTeamWon: $whichTeamWon")
            if (whichTeamWon == 1) {
                matchDoc.update("team1Won", true).await()
                Log.d("FirebaseRepository", "Updated team1Won to true")
            } else if (whichTeamWon == 2) {
                matchDoc.update("team2Won", true).await()
                Log.d("FirebaseRepository", "Updated team2Won to true")
            } else {
                Log.e("FirebaseRepository", "Invalid value for whichTeamWon: $whichTeamWon")
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating match result", e)
        }

        // Check if all matches in the groups are finished
        val tournament = loadTournamentById(tournamentId)
        val allMatchesFinished = tournament?.matches?.all { it.team1Won == true || it.team2Won == true } ?: false

        if (allMatchesFinished) {
            // Generate new matches for the losers bracket
            val losersBracketMatches = tournament?.let { generateLosersBracketMatches(it) }
            // Update the tournament with the new matches
            if (losersBracketMatches != null) {
                updateTournamentWithLosersBracket(tournamentId, losersBracketMatches)
            }
        }
    }

    private fun generateLosersBracketMatches(tournament: Tournament): List<MatchDetails> {
        val losersBracketMatches = mutableListOf<MatchDetails>()
        val worstTeams = getWorstTeams(tournament)

        // Each of the two worst teams from each group plays two matches
        for (i in worstTeams.indices step 2) {
            val team1 = worstTeams[i]
            val team2 = worstTeams[i + 1]
            losersBracketMatches.add(MatchDetails(team1 = team1, team2 = team2))
            losersBracketMatches.add(MatchDetails(team1 = team2, team2 = team1))
        }

        return losersBracketMatches
    }

    suspend fun loadMatchDetails(tournamentId: String, matchId: String): MatchDetails? {
        return try {
            val groupStageMatchDoc = database.collection("tournaments")
                .document(tournamentId)
                .collection("group stages")
                .document(matchId)
                .get()
                .await()

            val losersBracketMatchDoc = database.collection("tournaments")
                .document(tournamentId)
                .collection("losers bracket")
                .document(matchId)
                .get()
                .await()

            val groupStageMatch = groupStageMatchDoc.toObject(MatchDetails::class.java)?.copy(id = groupStageMatchDoc.id)
            val losersBracketMatch = losersBracketMatchDoc.toObject(MatchDetails::class.java)?.copy(id = losersBracketMatchDoc.id)

            groupStageMatch ?: losersBracketMatch
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                Log.d("Error loading match details", e.message.toString())
            }
            null
        }
    }
    suspend fun loadTournamentById(tournamentId: String): Tournament? {
        return try {
            val tournamentDoc = database.collection("tournaments").document(tournamentId).get().await()
            tournamentDoc.toObject(Tournament::class.java)?.copy(
                id = tournamentDoc.id,
                matches = loadMatchesFromTournament(tournamentDoc.id)
            )
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                Log.d("Error loading tournament", e.message.toString())
            }
            null
        }
    }

    private suspend fun updateTournamentWithLosersBracket(tournamentId: String, losersBracketMatches: List<MatchDetails>) {
        val tournamentDoc = database.collection("tournaments").document(tournamentId)
        val losersBracketCollection = tournamentDoc.collection("losers_bracket")

        try {
            losersBracketMatches.forEach { match ->
                val matchData = mapOf(
                    "id" to match.id,
                    "team1" to match.team1?.let { team ->
                        mapOf(
                            "id" to team.id,
                            "teamName" to team.teamName,
                            "member1" to team.member1?.let { member ->
                                mapOf(
                                    "name" to member.name,
                                    "cupsHit" to member.cupsHit,
                                    "redemptions" to member.redemptions,
                                    "trickshots" to member.trickshots
                                )
                            },
                            "member2" to team.member2?.let { member ->
                                mapOf(
                                    "name" to member.name,
                                    "cupsHit" to member.cupsHit,
                                    "redemptions" to member.redemptions,
                                    "trickshots" to member.trickshots
                                )
                            }
                        )
                    },
                    "team1Won" to match.team1Won,
                    "team2" to match.team2?.let { team ->
                        mapOf(
                            "id" to team.id,
                            "teamName" to team.teamName,
                            "member1" to team.member1?.let { member ->
                                mapOf(
                                    "name" to member.name,
                                    "cupsHit" to member.cupsHit,
                                    "redemptions" to member.redemptions,
                                    "trickshots" to member.trickshots
                                )
                            },
                            "member2" to team.member2?.let { member ->
                                mapOf(
                                    "name" to member.name,
                                    "cupsHit" to member.cupsHit,
                                    "redemptions" to member.redemptions,
                                    "trickshots" to member.trickshots
                                )
                            }
                        )
                    },
                    "team2Won" to match.team2Won,
                    "round" to match.round
                )
                losersBracketCollection.document(match.id).set(matchData).await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating tournament with losers bracket", e)
        }
    }

    private fun getWorstTeams(tournament: Tournament): List<TeamDetails> {
        // Logic to determine the two worst teams from each group
        // For simplicity, let's assume the worst teams are those with the least wins
        val teamWins = mutableMapOf<String, Int>()

        tournament.matches.forEach { match ->
            match.team1?.let { team ->
                teamWins[team.id] = teamWins.getOrDefault(team.id, 0) + if (match.team1Won == true) 1 else 0
            }
            match.team2?.let { team ->
                teamWins[team.id] = teamWins.getOrDefault(team.id, 0) + if (match.team2Won == true) 1 else 0
            }
        }

        val sortedTeams = teamWins.entries.sortedBy { it.value }
        val worstTeams = sortedTeams.take(2).map { entry ->
            tournament.matches.flatMap { listOfNotNull(it.team1, it.team2) }.first { it.id == entry.key }
        }

        return worstTeams
    }

 */
