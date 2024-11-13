package com.example.ubkk3

import GoogleAuthUiClient
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ubkk3.firebaseSignIn.SignInScreen
import com.example.ubkk3.firebaseSignIn.SignInViewModel
import com.example.ubkk3.navigation.TabItem
import com.example.ubkk3.ui.Profile.ProfileScreen
import com.example.ubkk3.ui.stats.Stats
import com.example.ubkk3.ui.theme.UBKK3Theme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.ubkk3.ui.admin.AdminScreen
import com.example.ubkk3.ui.admin.CreateTournamentScreen
import com.example.ubkk3.ui.admin.CreateTournamentViewModel
import com.example.ubkk3.ui.tournament.MatchScreen
import com.example.ubkk3.ui.tournament.TournamentScreen
import com.example.ubkk3.ui.tournament.TournamentScreenViewModel
import com.example.ubkk3.ui.tournament.Tournaments
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Firebase.database.setPersistenceEnabled(true)
        setContent {
            UBKK3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()
                    val createTournamentViewModel = viewModel<CreateTournamentViewModel>()
                    val tournamentScreenViewModel = viewModel<TournamentScreenViewModel>()
                    val tournaments = tournamentScreenViewModel.activeTournaments.collectAsStateWithLifecycle().value
                    val tournamentState by tournamentScreenViewModel.tournamentState.collectAsState()
                    val onTournamentEvent = tournamentScreenViewModel::onEvent


                    NavHost(navController = navController, startDestination = "sign_in") {
                        composable("sign_in") {
                            val signInViewModel = viewModel<SignInViewModel>()
                            val state by signInViewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("logged_in")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.getSignInWithIntent(result.data ?: return@launch)
                                            signInViewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("logged_in")
                                    signInViewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable("logged_in") {

                            val tabItems = listOf(
                                TabItem(
                                    title = "Tournaments",
                                    unselectedIcon = ImageVector.vectorResource(id = R.drawable.tournament),
                                    selectedIcon = ImageVector.vectorResource(id = R.drawable.tournament_selected)
                                ),
                                TabItem(
                                    title = "Stats",
                                    unselectedIcon = ImageVector.vectorResource(id = R.drawable.stats),
                                    selectedIcon = ImageVector.vectorResource(id = R.drawable.stats_selected)
                                ),
                                if(googleAuthUiClient.getSignedInUser()?.isAdmin == true){
                                    TabItem(
                                        title = "Admin",
                                        unselectedIcon = ImageVector.vectorResource(id = R.drawable.admin),
                                        selectedIcon = ImageVector.vectorResource(id = R.drawable.admin_selected)
                                    )
                                } else {
                                    TabItem(
                                        title = "Profile",
                                        unselectedIcon = ImageVector.vectorResource(id = R.drawable.profile),
                                        selectedIcon = ImageVector.vectorResource(id = R.drawable.profile_selected)
                                    )
                                }
                            )

                            var selectedTabIndex by remember {
                                mutableIntStateOf(0)
                            }
                            val pagerState = rememberPagerState {
                                tabItems.size
                            }
                            LaunchedEffect(selectedTabIndex) {
                                pagerState.animateScrollToPage(selectedTabIndex)
                            }

                            //checks if a scroll is in progress, used to avoid trouble when scrolling
                            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                                if(!pagerState.isScrollInProgress) {
                                    selectedTabIndex = pagerState.currentPage
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalAlignment = Alignment.Bottom,
                                ) {index ->
                                    when(index){
                                        0 -> TournamentScreen(
                                            navController = navController,
                                            tournamentState = tournamentState,
                                            onTournamentEvent = onTournamentEvent
                                        )
                                        1 -> Stats()
                                        2 -> if (googleAuthUiClient.getSignedInUser()?.isAdmin == true){
                                                AdminScreen(
                                                    navController = navController,
                                                    userData = googleAuthUiClient.getSignedInUser(),
                                                    onSignOut = {
                                                        lifecycleScope.launch {
                                                            googleAuthUiClient.signOut()
                                                            Toast.makeText(
                                                                applicationContext,
                                                                "Signed out",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navController.navigate("sign_in")
                                                        }
                                                    },
                                                )
                                            } else {
                                                ProfileScreen(
                                                    userData = googleAuthUiClient.getSignedInUser(),
                                                    onSignOut = {
                                                        lifecycleScope.launch {
                                                            googleAuthUiClient.signOut()
                                                            Toast.makeText(
                                                                applicationContext,
                                                                "Signed out",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navController.navigate("sign_in")
                                                        }
                                                    },
                                                )
                                            }
                                    }
                                }
                                TabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    containerColor = Color.DarkGray,

                                    //indicator to avoid ugly line under selected tab
                                    indicator = {
                                        Color.Transparent
                                    }
                                ) {
                                    tabItems.forEachIndexed { index, item ->
                                        Tab(
                                            selected = selectedTabIndex == index,
                                            onClick = { selectedTabIndex = index },
                                            text = { Text(item.title) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selectedTabIndex == index) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = null
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        composable(
                            "match/{matchDetailsJson}/{selectedTournament}",
                            arguments = listOf(
                                navArgument("matchDetailsJson") { type = NavType.StringType },
                                navArgument("selectedTournament") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            val userData = googleAuthUiClient.getSignedInUser()
                            val matchDetailsJson = backStackEntry.arguments?.getString("matchDetailsJson") ?: ""
                            val selectedTournamentJson = backStackEntry.arguments?.getString("selectedTournament") ?: ""
                            MatchScreen(navController, Uri.decode(matchDetailsJson), userData, Uri.decode(selectedTournamentJson))
                        }
                        composable(
                            "create_tournament/{title}",
                            arguments = listOf(
                                navArgument("title") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: ""
                            CreateTournamentScreen(navController = navController, title = title)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UBKK3Theme {
        Greeting("Android")
    }
}