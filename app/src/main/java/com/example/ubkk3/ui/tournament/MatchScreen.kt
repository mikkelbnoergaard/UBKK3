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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ubkk3.R
import com.example.ubkk3.firebaseSignIn.UserData
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.state.TournamentState
import com.example.ubkk3.ui.event.TournamentEvent
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(
    navController: NavController,
    matchDetailsJson: String,
    userData: UserData?,
    selectedTournamentJson: String,
    tournamentState: TournamentState,
    onTournamentEvent: (TournamentEvent) -> Unit
) {


    val showDialog = remember { mutableStateOf(false) }

    val viewModel: TournamentScreenViewModel = viewModel()
    // val selectedTournament by rememberUpdatedState(tournamentState.selectedTournament)
    val matchDetails by rememberUpdatedState(tournamentState.matchDetails)
    val selectedTournament by rememberUpdatedState(tournamentState.selectedTournament)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text(text = matchDetails?.team1?.teamName + "   VS   " + matchDetails?.team2?.teamName) },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            var team1color = Color.White
            var team2color = Color.White

            if(matchDetails?.team1Won == true) {
                team1color = Color.Green
            }
            if(matchDetails?.team2Won == true) {
                team2color = Color.Green
            }
            Column() {
                matchDetails?.team1?.teamName?.let { Text(text = it, color = team1color) }
                matchDetails?.team1?.member1?.name.let {
                    if (it != null) {
                        Text(text = it, color = team1color)
                    }
                }
                matchDetails?.team1?.member2?.name.let {
                    if (it != null) {
                        Text(text = it, color = team1color)
                    }
                }
            }
            Column() {
                matchDetails?.team2?.teamName?.let { Text(text = it, color = team2color) }
                matchDetails?.team2?.member1?.name.let {
                    if (it != null) {
                        Text(text = it, color = team2color)
                    }
                }
                matchDetails?.team2?.member2?.name.let {
                    if (it != null) {
                        Text(text = it, color = team2color)
                    }
                }
            }
        }
        if (userData != null) {
            if(userData.isAdmin) {
                Button(
                    onClick = {
                        //TODO: Handle onClick
                    }
                ) {
                    Text(text = "Edit Match")
                }
            }
            if(matchDetails?.team1Won == false && matchDetails?.team2Won == false) {
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
    if (showDialog.value) {
        SelectWinnerDialog(
            onDismiss = { showDialog.value = false },
            onSelectWinner = { whichTeamWon ->
                selectedTournament?.let {
                    viewModel.viewModelScope.launch {
                        matchDetails?.let { it1 -> onTournamentEvent(TournamentEvent.UpdateMatchWinner(it.id, it1.id, whichTeamWon)) }
                    }
                }
                showDialog.value = false
            },
            match = matchDetails!!,
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
                    onSelectWinner(1)
                    onDismiss()
                    navController.popBackStack()
                }
            ) {
                Text(match.team1?.teamName.toString())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSelectWinner(2)
                    onDismiss()
                    navController.popBackStack()
                }
            ) {
                Text(match.team2?.teamName.toString())
            }
        }
    )
}