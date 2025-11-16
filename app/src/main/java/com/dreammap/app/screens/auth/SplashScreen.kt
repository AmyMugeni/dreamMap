package com.dreammap.app.screens.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
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

    // --- TEMP Logout & Debug Navigation ---
    var tempRole by remember { mutableStateOf("mentor") } // Change to "student" to test student flow
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        // Force logout at start
        authViewModel.logout()
        Log.d("SplashScreen", "Logging out user...")

        // Wait until currentUser state actually updates to null
        while (currentUser != null) {
            delay(50)
        }

        // Optional extra splash time
        delay(500)

        // Navigate based on hardcoded tempRole
        when (tempRole) {
            "student" -> {
                Log.d("SplashScreen", "Navigating to Student Dashboard")
                navController.navigate(Screen.HomeGraph.Dashboard.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            "mentor" -> {
                Log.d("SplashScreen", "Navigating to Mentor Dashboard")
                navController.navigate(Screen.MentorGraph.Dashboard.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            else -> {
                Log.d("SplashScreen", "Navigating to Auth Graph")
                navController.navigate(Screen.AuthGraph.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }
}
