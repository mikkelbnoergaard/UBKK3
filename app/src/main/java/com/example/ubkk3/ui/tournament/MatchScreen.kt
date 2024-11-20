package com.example.ubkk3.ui.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.ubkk3.R
import com.example.ubkk3.firebaseSignIn.UserData
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.state.TournamentState
import com.example.ubkk3.ui.event.TournamentEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDialog(
    onDismiss: () -> Unit,
    navController: NavController,
    selectedMatch: MatchDetails,
    selectedTournament: Tournament,
    team1: TeamDetails,
    team2: TeamDetails,
    players: Map<Int, List<Player>>,
    userData: UserData?,
    onTournamentEvent: (TournamentEvent) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
            navController.popBackStack()
        },
        title = {
            Text(text = "${team1.teamName}   VS   ${team2.teamName}")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    var team1color = Color.White
                    var team2color = Color.White

                    if (selectedMatch.team1Won == true) {
                        team1color = Color.Green
                    }
                    if (selectedMatch.team2Won == true) {
                        team2color = Color.Green
                    }
                    Column {
                        Text(text = team1.teamName, color = team1color)
                        players[team1.id]?.forEach { player ->
                            Text(text = player.name, color = team1color)
                        }
                    }
                    Column {
                        Text(text = team2.teamName, color = team2color)
                        players[team2.id]?.forEach { player ->
                            Text(text = player.name, color = team2color)
                        }
                    }
                }
                if (userData != null) {
                    if (userData.isAdmin) {
                        Button(
                            onClick = {
                                //TODO: Handle onClick
                            }
                        ) {
                            Text(text = "Edit Match")
                        }
                    }
                    if (selectedMatch.team1Won == null && selectedMatch.team2Won == null) {
                        Button(
                            onClick = {
                                showDialog.value = true
                            }
                        ) {
                            Text(text = "Select winner")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedMatch.team1Won == null && selectedMatch.team2Won == null) {
                Button(
                    onClick = {
                        showDialog.value = true
                    }
                ) {
                    Text(text = "Select winner")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialog.value = false
                    navController.popBackStack()
                }
            ) {
                Text(text = "Close")
            }
        }
    )

    if (showDialog.value) {
        SelectWinnerDialog(
            onDismiss = { showDialog.value = false },
            onSelectWinner = { whichTeamWon ->
                onTournamentEvent(
                    TournamentEvent.UpdateMatchWinner(
                        selectedMatch.tournamentId,
                        selectedMatch.id,
                        if (whichTeamWon == 0) team1.id else team2.id
                    )
                )
                showDialog.value = false
            },
            team1 = team1,
            team2 = team2,
            navController = navController
        )
    }
}

@Composable
fun SelectWinnerDialog(
    onDismiss: () -> Unit,
    onSelectWinner: (whichTeamWon: Int) -> Unit,
    team1: TeamDetails,
    team2: TeamDetails,
    navController: NavController
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select winner") },
        dismissButton = {
            Button(
                onClick = {
                    onSelectWinner(0)
                    onDismiss()
                    navController.popBackStack()
                }
            ) {
                Text(team1.teamName)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSelectWinner(1)
                    onDismiss()
                    navController.popBackStack()
                }
            ) {
                Text(team2.teamName)
            }
        }
    )
}