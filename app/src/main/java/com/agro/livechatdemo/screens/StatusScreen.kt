package com.agro.livechatdemo.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.agro.livechatdemo.LCViewModel

@Composable
fun StatusScreen(navController: NavHostController, vm: LCViewModel) {
    BottomNavigationMenu(selectedItem = BottomNavigationItem.STATUSLIST, navController = navController)
}