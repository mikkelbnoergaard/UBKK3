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
                Text(text = selectedMatch.team1.teamName + "   VS   " + selectedMatch.team2.teamName)
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

                        if (selectedMatch.team1Won) {
                            team1color = Color.Green
                        }
                        if (selectedMatch.team2Won) {
                            team2color = Color.Green
                        }
                        Column {
                            selectedMatch.team1.teamName.let { Text(text = it, color = team1color) }
                            selectedMatch.team1.player1.name.let { Text(text = it, color = team1color) }
                            selectedMatch.team1.player2.name.let { Text(text = it, color = team1color) }
                        }
                        Column {
                            selectedMatch.team2.teamName.let { Text(text = it, color = team2color) }
                            selectedMatch.team2.player1.name.let { Text(text = it, color = team2color) }
                            selectedMatch.team2.player2.name.let { Text(text = it, color = team2color) }
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
                        if (!selectedMatch.team1Won && !selectedMatch.team2Won) {
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
                if (!selectedMatch.team1Won && !selectedMatch.team2Won) {
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
                        tournamentId = selectedTournament.id,
                        matchId = selectedMatch.id,
                        winningTeam = whichTeamWon
                    )
                )
                showDialog.value = false
            },
            match = selectedMatch,
            navController = navController
        )
    }
}

@Composable
fun SelectWinnerDialog(
    onDismiss: () -> Unit,
    onSelectWinner: (whichTeamWon: Int) -> Unit,
    match: MatchDetails,
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
                Text(match.team1.teamName)
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
                Text(match.team2.teamName)
            }
        }
    )
}