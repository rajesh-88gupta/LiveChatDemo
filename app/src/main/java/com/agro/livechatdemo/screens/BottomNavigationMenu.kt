package com.agro.livechatdemo.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.agro.livechatdemo.DestinationScreen
import com.agro.livechatdemo.R
import com.agro.livechatdemo.ui.theme.navigateTo

enum class BottomNavigationItem(val icon: Int,val navDestinationScreen: DestinationScreen){
    CHATLIST(R.drawable.chat,DestinationScreen.ChatList),
    STATUSLIST(R.drawable.reload,DestinationScreen.StatusList),
    PROFILE(R.drawable.profile,DestinationScreen.Profile)
}
@Composable
fun BottomNavigationMenu(
    selectedItem:BottomNavigationItem,
    navController: NavController
) {
    Row(modifier = Modifier
        .fillMaxSize()
        .wrapContentHeight()
        .padding(top = 4.dp)
        .background(Color.White)) {

        for (item in BottomNavigationItem.values()){
            Image(painter = painterResource(id = item.icon), contentDescription = null, modifier = Modifier.size(40.dp)
                .padding(4.dp).weight(1f).clickable {
                    navigateTo(navController,item.navDestinationScreen.route)
                }, colorFilter = if (item == selectedItem)
                ColorFilter.tint(color = Color.Blue)
                else
                ColorFilter.tint(Color.Gray)
            )
        }
    }
}