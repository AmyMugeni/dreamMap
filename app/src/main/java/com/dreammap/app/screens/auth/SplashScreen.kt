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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dreammap.app.Screen // Import your navigation routes
import com.dreammap.app.ui.theme.IdeaYellow // Import the accent color

@Composable
fun SplashScreen(
    navController: NavHostController,
    // Get the shared ViewModel instance from the NavHost scope
    authViewModel: AuthViewModel = viewModel()
) {
    // Collect the states we need to decide navigation
    val user by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // --- Navigation Logic ---
    LaunchedEffect(user, isLoading) {
        // This LaunchedEffect runs whenever 'user' or 'isLoading' changes.

        // We only navigate AFTER the initial check is complete (isLoading == false)
        if (!isLoading) {
            val destination = when {
                user == null -> Screen.AuthGraph.route // Logged out -> Auth Flow
                user?.role == "admin" -> Screen.AdminDashboard.route // Admin -> Admin Dash
                else -> Screen.HomeGraph.route // Student/Mentor -> Main App
            }

            // Navigate and remove the entire backstack beneath the new destination (inclusive = true)
            // This prevents the user from navigating back to the splash screen.
            navController.navigate(destination) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    // --- UI Structure (The "Seeds of Dreams" Look) ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Use the NavyBlue from your custom theme defined in MainActivity
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for the Animated Logo
        Text(
            text = "DreamMap",
            color = MaterialTheme.colorScheme.onBackground, // Should be white/light color on navy
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // You would replace this Text composable with your actual animation,
        // likely involving Lottie or a complex Compose animation,
        // using IdeaYellow and LightCyan as highlights.
        //
    }
}