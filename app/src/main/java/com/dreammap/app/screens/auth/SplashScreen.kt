package com.dreammap.app.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen
import kotlinx.coroutines.delay
import android.util.Log

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "DreamMap",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }

    // --- Navigation Logic ---
//    LaunchedEffect(Unit) {
//        delay(1500)
//        val user = authViewModel.currentUser.value
//        val isLoading = authViewModel.isLoading.value
//
//        if (!isLoading) {
//            when {
//                user == null -> {
//                    navController.navigate(Screen.AuthGraph.route) {
//                        popUpTo(Screen.Splash.route) { inclusive = true }
//                    }
//                }
//                user.role == "admin" -> {
//                    navController.navigate(Screen.AdminDashboard.route) {
//                        popUpTo(Screen.Splash.route) { inclusive = true }
//                    }
//                }
//                else -> {
//                    navController.navigate(Screen.HomeGraph.route) {
//                        popUpTo(Screen.Splash.route) { inclusive = true }
//                    }
//                }
//            }
//        }
//    }
    LaunchedEffect(Unit) {
        delay(1500)
        navController.navigate(Screen.AuthGraph.route) {
            Log.d("SplashScreen", "Navigating to AuthGraph...")

            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

}
