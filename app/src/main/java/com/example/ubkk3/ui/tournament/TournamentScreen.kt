package com.example.ubkk3.ui.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Tournament
import com.example.ubkk3.state.TournamentState
import com.example.ubkk3.ui.event.TournamentEvent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun TournamentScreen(
    navController: NavController,
    tournamentState: TournamentState,
    onTournamentEvent: (TournamentEvent) -> Unit
) {

    val selectedTournament by rememberUpdatedState(tournamentState.selectedTournament)

    Column(
        verticalArrangement = Arrangement.Top
    ) {

        TopBar(
            activeTournaments = tournamentState.activeTournaments,
            tournamentState,
            onTournamentEvent,
            onSelectTournament = { onTournamentEvent(TournamentEvent.SelectTournament(it)) }
        )

        selectedTournament?.let {
            Tournaments(
                navController = navController,
                tournament = it,
                tournamentState = tournamentState,
                onTournamentEvent = onTournamentEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    activeTournaments: List<Tournament>,
    tournamentState: TournamentState,
    onTournamentEvent: (TournamentEvent) -> Unit,
    onSelectTournament: (Tournament) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = tournamentState.selectedTournament?.tournamentName ?: "Select Tournament")
        },
        actions = {
            Box {
                IconButton(onClick = {
                    expanded = true
                    onTournamentEvent(TournamentEvent.FetchTournaments)
                }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Tournament")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    activeTournaments.forEach { tournament ->
                        DropdownMenuItem(
                            text = { Text(tournament.tournamentName) },
                            onClick = {
                                onTournamentEvent(TournamentEvent.SelectTournament(tournament))
                                onSelectTournament(tournament)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun Tournaments(
    navController: NavController,
    tournament: Tournament,
    tournamentState: TournamentState,
    onTournamentEvent: (TournamentEvent) -> Unit
) {

    val matches = tournament.matches
    if (matches.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No matches available in this tournament.")
        }
    } else {
        var scale by remember { mutableStateOf(tournamentState.zoomLevel) }
        var offsetXState by remember { mutableStateOf(tournamentState.offsetX) }
        var offsetYState by remember { mutableStateOf(tournamentState.offsetY) }

        val groupCount = matches.groupBy { it.round }.size
        val matchCount = matches.size
        val canvasWidth = (groupCount * 325).dp

        val canvasHeight = if (groupCount == 0) {
            400.dp
        } else {
            (matchCount * 150 / groupCount).dp
        }

        val minScale = 0.2f
        val maxScale = 1f

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(minScale, maxScale)
                        offsetXState += pan.x
                        offsetYState += pan.y
                        onTournamentEvent(TournamentEvent.UpdateZoomLevel(scale))
                        onTournamentEvent(TournamentEvent.UpdateOffsetX(offsetXState))
                        onTournamentEvent(TournamentEvent.UpdateOffsetY(offsetYState))
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(canvasWidth, canvasHeight)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetXState,
                        translationY = offsetYState
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = tournament.tournamentName)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    matches.filter { it.round!!.startsWith("Group") }.groupBy { it.round }
                        .forEach { (round, groupMatches) ->
                            Column(
                                modifier = Modifier
                                    .width(300.dp)
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (round != null) {
                                    Text(text = round, modifier = Modifier.padding(8.dp))
                                }
                                groupMatches.forEach { match ->
                                    Match(
                                        matchDetails = match,
                                        onClick = { matchDetails ->
                                            val matchDetailsJson = Json.encodeToString(matchDetails)
                                            val selectedTournament = Json.encodeToString(tournament)
                                            onTournamentEvent(TournamentEvent.UpdateMatchDetails(match.id))
                                            navController.navigate("match/$matchDetailsJson/$selectedTournament")
                                        }
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun Match(
    matchDetails: MatchDetails,
    modifier: Modifier = Modifier,
    onClick: (MatchDetails) -> Unit
) {
    Column(
        modifier = modifier
            .size(300.dp, 100.dp)
            .padding(8.dp)
            .background(Color.DarkGray)
            .border(1.dp, Color.Black)
            .clickable { onClick(matchDetails) }
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val team1Color = if (matchDetails.team1Won == true) Color.Green else Color.White
                matchDetails.team1?.teamName?.let {
                    Text(
                        text = it,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = team1Color
                    )
                }
                matchDetails.team1?.member1?.name.let {
                    if (it != null) {
                        Text(
                            text = it,
                            color = team1Color
                        )
                    }
                }
                matchDetails.team1?.member2?.name.let {
                    if (it != null) {
                        Text(
                            text = it,
                            color = team1Color
                        )
                    }
                }
            }
            Text(text = "VS", modifier = Modifier.align(Alignment.CenterVertically))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val team2Color = if (matchDetails.team2Won == true) Color.Green else Color.White
                matchDetails.team2?.teamName.let {
                    if (it != null) {
                        Text(
                            text = it,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = team2Color
                        )
                    }
                }
                matchDetails.team2?.member1?.name.let {
                    if (it != null) {
                        Text(
                            text = it,
                            color = team2Color
                        )
                    }
                }
                matchDetails.team2?.member2?.name.let {
                    if (it != null) {
                        Text(
                            text = it,
                            color = team2Color
                        )
                    }
                }
            }
        }
    }
}