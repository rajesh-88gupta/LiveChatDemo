package com.agro.livechatdemo.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

import com.agro.livechatdemo.DestinationScreen
import com.agro.livechatdemo.LCViewModel

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun checkSignedIn(vm: LCViewModel, navController: NavController) {
val alreadySignIn =remember{
    mutableStateOf(false)
    }
    val signIn =vm.signIn.value
    if(signIn && !alreadySignIn.value){
        alreadySignIn.value =true
        navController.navigate(DestinationScreen.ChatList.route){
            popUpTo(0)
        }

    }
}
@Composable
fun CommonDivider(){
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}
@Composable
fun CommonImage(data:String?,
                modifier: Modifier=Modifier.wrapContentSize(),
                contentScale: ContentScale =ContentScale.Crop){
    val painter = rememberAsyncImagePainter(model = data)
    Image(painter = painter, contentDescription = null, modifier = modifier, contentScale = contentScale)

}

@Composable
fun CommonProgressBar() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) {}
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun TitleText(text:String){
Text(text = text, fontWeight = FontWeight.Bold, fontSize = 35.sp, modifier = Modifier.padding(8.dp))
}