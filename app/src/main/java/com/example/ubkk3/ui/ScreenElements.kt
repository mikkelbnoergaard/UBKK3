package com.example.ubkk3.ui

import androidx.compose.runtime.Composable


@Composable
fun GeneralLayout(

) {
    /*

    val tabItems = listOf(
        TabItem(
            title = "Tournaments",
            unselectedIcon = R.drawable.tournament,
            selectedIcon = R.drawable.tournament_selected
        ),
        TabItem(
            title = "Players",
            unselectedIcon = R.drawable.players,
            selectedIcon = R.drawable.players_selected
        ),
        TabItem(
            title = "Stats",
            unselectedIcon = R.drawable.stats,
            selectedIcon = R.drawable.stats_selected
        ),
        TabItem(
            title = "Profile",
            unselectedIcon = R.drawable.profile,
            selectedIcon = R.drawable.profile_selected
        )
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
            verticalAlignment = Alignment.Bottom
        ) {index ->
            when(index){
                0 -> ProfileScreen(
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        lifecycleScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(
                                applicationContext,
                                "Signed out successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate("sign_in")
                        }
                    }
                ) //tournaments screen
                1 -> ProfileScreen() //tournaments screen
                2 -> ProfileScreen() //tournaments screen
                3 -> ProfileScreen() //tournaments screen
                else -> ProfileScreen() //tournaments screen
            }
        }
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.tertiary,

            //indicator to avoid ugly line under selected tab
            indicator = {
                Color.Transparent
            }
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier,
                    unselectedContentColor = MaterialTheme.colorScheme.onSecondary,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    icon = {
                        Icon(
                            modifier = Modifier,
                            imageVector = if(index == selectedTabIndex){
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }
    }

     */

}



