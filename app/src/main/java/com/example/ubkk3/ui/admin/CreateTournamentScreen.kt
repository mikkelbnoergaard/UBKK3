package com.example.ubkk3.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ubkk3.R
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.state.AdminState
import com.example.ubkk3.ui.event.AdminEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(
    navController: NavController,
    title: String,
    adminState: AdminState,
    onAdminEvent: (AdminEvent) -> Unit
) {
    val teams by rememberUpdatedState(adminState.createTournamentTeams)
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val tournamentTitle = remember { mutableStateOf(title) }
    var currentTeam by rememberSaveable { mutableStateOf<TeamDetails?>(null) }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var showConfirmCreateDialog by remember { mutableStateOf(false) }
    var showChangeTitleDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text(text = "Creating tournament - T: ${teams.size}") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
        )
        Row(
            modifier = Modifier.clickable {

            }
        ) {

        }

        Button(
            onClick = { showChangeTitleDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(text = tournamentTitle.value)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(teams) { team ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        currentTeam = team
                        isEditing = true
                        showDialog = true
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = team.teamName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val players = adminState.createTournamentPlayers.filter { it.teamId == team.id }
                            players.forEach { player ->
                                Text(text = player.name)
                            }
                        }
                    }
                }
            }

        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(text = "Add Team")
        }

        Button(
            onClick = { showConfirmCreateDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(text = "Create Tournament")
        }

        Button(
            onClick = { onAdminEvent(AdminEvent.AddTestTeams) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(text = "Add 8 Test Teams")
        }
    }

    if (showDialog) {
        currentTeam?.let {
            val players = adminState.createTournamentPlayers.filter { it.teamId == it.id }
            TeamDialog(
                team = it,
                players = players,
                onDismiss = { showDialog = false },
                onSave = { team, updatedPlayers ->
                    if (isEditing) {
                        onAdminEvent(AdminEvent.UpdateTeam(team))
                        updatedPlayers.forEach { player ->
                            onAdminEvent(AdminEvent.UpdatePlayer(player))
                        }
                    } else {
                        onAdminEvent(AdminEvent.AddTeam(team))
                        updatedPlayers.forEach { player ->
                            onAdminEvent(AdminEvent.AddPlayer(player))
                        }
                    }
                    showDialog = false
                },
                onDelete = { team ->
                    onAdminEvent(AdminEvent.DeleteTeam(team))
                    showDialog = false
                }
            )
        }
    }

    if (showConfirmCreateDialog) {
        ConfirmCreateTournamentDialog(
            onConfirm = {
                if (teams.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        onAdminEvent(AdminEvent.GenerateMatches)
                        val tournament = Tournament(
                            tournamentName = adminState.createTournamentName,
                            isActive = true
                        )
                        onAdminEvent(AdminEvent.SaveTournamentInDatabase(tournament, adminState.createTournamentMatches, teams, adminState.createTournamentPlayers))
                        withContext(Dispatchers.Main) {
                            navController.navigate("logged_in")
                        }
                    }
                }
                showConfirmCreateDialog = false
            },
            onDismiss = { showConfirmCreateDialog = false }
        )
    }

    if (showChangeTitleDialog) {
        ChangeEventTitleDialog(
            currentTitle = tournamentTitle.value,
            onDismiss = { showChangeTitleDialog = false },
            onSave = { newTitle ->
                tournamentTitle.value = newTitle
                showChangeTitleDialog = false
                onAdminEvent(AdminEvent.SetTournamentTitle(newTitle))
            }
        )
    }
}

@Composable
fun ConfirmCreateTournamentDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Tournament Creation") },
        text = { Text(text = "Are you sure you want to create the tournament?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDialog(
    team: TeamDetails,
    players: List<Player>,
    onDismiss: () -> Unit,
    onSave: (TeamDetails, List<Player>) -> Unit,
    onDelete: (TeamDetails) -> Unit,
) {
    var teamName by remember { mutableStateOf(team.teamName) }

    var player1Name by remember { mutableStateOf(players.getOrNull(0)?.name ?: "") }
    var player1Email by remember { mutableStateOf(players.getOrNull(0)?.email ?: "") }
    var player2Name by remember { mutableStateOf(players.getOrNull(1)?.name ?: "") }
    var player2Email by remember { mutableStateOf(players.getOrNull(1)?.email ?: "") }

    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    if (showConfirmDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                onDelete(team)
                showConfirmDeleteDialog = false
                onDismiss()
            },
            onDismiss = { showConfirmDeleteDialog = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Team Details") },
        text = {
            Column {
                TextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team Name") }
                )
                TextField(
                    value = player1Name,
                    onValueChange = { player1Name = it },
                    label = { Text("Player 1 Name") }
                )
                TextField(
                    value = player1Email,
                    onValueChange = { player1Email = it },
                    label = { Text("Player 1 Email") }
                )
                TextField(
                    value = player2Name,
                    onValueChange = { player2Name = it },
                    label = { Text("Player 2 Name") }
                )
                TextField(
                    value = player2Email,
                    onValueChange = { player2Email = it },
                    label = { Text("Player 2 Email") }
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { showConfirmDeleteDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash_can),
                        contentDescription = "Delete"
                    )
                }
                Row {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Button(
                        onClick = {
                            val updatedPlayers = listOf(
                                Player(
                                    id = players.getOrNull(0)?.id ?: 0,
                                    teamId = team.id,
                                    name = player1Name,
                                    email = player1Email
                                ),
                                Player(
                                    id = players.getOrNull(1)?.id ?: 0,
                                    teamId = team.id,
                                    name = player2Name,
                                    email = player2Email
                                )
                            )
                            onSave(
                                team.copy(teamName = teamName),
                                updatedPlayers
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        },
        dismissButton = {}
    )
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Deletion") },
        text = { Text(text = "Are you sure you want to delete this team?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEventTitleDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val titleState = remember { mutableStateOf(TextFieldValue(currentTitle)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Change Event Title") },
        text = {
            OutlinedTextField(
                value = titleState.value,
                onValueChange = { titleState.value = it },
                label = { Text(text = "Event Title") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(titleState.value.text)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}