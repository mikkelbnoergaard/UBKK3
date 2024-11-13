package com.example.ubkk3.ui.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ubkk3.firebaseSignIn.UserData
import com.example.ubkk3.match.Tournament

@Composable
fun AdminScreen(
    userData: UserData?,
    navController: NavController,
    onSignOut: () -> Unit,
) {
    val viewModel = viewModel<AdminScreenViewModel>()
    val tournaments by viewModel.tournaments.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            if (userData?.profilePictureUrl != null) {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (userData?.username != null) {
                Text(
                    text = userData.username,
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = userData.email)
                Spacer(modifier = Modifier.height(16.dp))

                if (userData.isAdmin) {
                    Text(text = "Admin")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showDialog.value = true }) {
                    Text(text = "Create new tournament")
                }
            }
            Button(onClick = onSignOut) {
                Text(text = "Sign Out")
            }
        }

        items(tournaments) { tournament ->
            TournamentListItem(tournament = tournament) { isActive ->
                viewModel.updateTournamentStatus(tournament.id, isActive)
            }
        }
    }

    if (showDialog.value) {
        CreateTournamentDialog(
            onDismiss = { showDialog.value = false },
            onCreate = { title ->
                // Handle the creation of the tournament with the specified title
                navController.navigate("create_tournament/$title")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    val viewModel: CreateTournamentViewModel = viewModel()
    val titleState = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create New Tournament") },
        text = {
            OutlinedTextField(
                value = titleState.value,
                onValueChange = { titleState.value = it },
                label = { Text("Tournament Title") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(titleState.value)
                    viewModel.saveTournamentToFirebase(titleState.value)
                    onDismiss()
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TournamentListItem(tournament: Tournament, onToggleActive: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = tournament.tournamentName,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        Switch(
            checked = tournament.isActive,
            onCheckedChange = onToggleActive,
            modifier = Modifier
                .padding(end = 16.dp)
        )
    }
}