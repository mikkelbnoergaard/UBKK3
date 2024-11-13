package com.example.ubkk3.navigation

import androidx.compose.ui.graphics.vector.ImageVector

//swipeable tab rows
data class TabItem(

    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector

)