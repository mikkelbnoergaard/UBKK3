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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FirebaseRepository(private val context: Context) {
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val sharedPreferences = context.getSharedPreferences("tournament_prefs", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadMatchesFromTournament(title: String): List<MatchDetails> {
        return try {
            val matchesRef = database.collection("tournaments").document(title).collection("matches")
            val querySnapshot = matchesRef.get().await()
            querySnapshot.documents.mapNotNull { document ->
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
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                Log.d("Error loading matches", e.message.toString())
            }
            emptyList()
        }
    }

    fun saveTournamentToFirebase(tournament: Tournament) {
        val tournamentData = mapOf(
            "id" to tournament.id,
            "tournamentName" to tournament.tournamentName,
            "isActive" to tournament.isActive
        )

        val tournamentDoc = database.collection("tournaments").document(tournament.id)
        tournamentDoc.set(tournamentData)
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Tournament details saved successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepository", "Error saving tournament details", exception)
            }

        val matchesCollection = tournamentDoc.collection("matches")
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
            .collection("matches")
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
    }
}