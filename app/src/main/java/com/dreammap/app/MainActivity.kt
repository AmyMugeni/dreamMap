package com.dreammap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.dreammap.app.data.repositories.AuthRepository
import com.dreammap.app.data.repositories.UserRepository
import com.dreammap.app.screens.auth.AuthViewModel
import com.dreammap.app.screens.auth.RoleSelectionScreen
import com.dreammap.app.screens.auth.SignUpScreen
import com.dreammap.app.screens.auth.LoginScreen
import com.dreammap.app.screens.auth.SplashScreen
import com.dreammap.app.ui.theme.DreamMapTheme

// --- 1. VIEWMODEL FACTORY (For Dependency Injection) ---

// NOTE: This factory is a basic way to inject the repository dependency into the ViewModel.
class DreamMapViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// --- 2. MAIN ACTIVITY ---

class MainActivity : ComponentActivity() {

    // Initialize Repositories (Will be provided to the ViewModel Factory)
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize repositories. Replace with Hilt/Koin for better structure later.
        val userRepository = UserRepository()
        authRepository = AuthRepository(userRepository = userRepository)

        setContent {
            // Apply the custom theme
            DreamMapTheme {
                // Pass the AuthViewModel factory to the root composable
                DreamMapNavRoot(
                    authViewModel = viewModel(
                        factory = DreamMapViewModelFactory(authRepository)
                    )
                )
            }
        }
    }
}

// --- 3. TOP-LEVEL NAVIGATION ROOT ---

@Composable
fun DreamMapNavRoot(authViewModel: AuthViewModel) {
    // Collect the user state and loading state
    val user by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    val navController = rememberNavController()

    // Determine the Start Destination based on authentication status
    val startDestination = remember(user, isLoading) {
        when {
            isLoading -> Screen.Splash.route
            user == null -> Screen.AuthGraph.route
            user?.role == "admin" -> Screen.AdminDashboard.route
            else -> Screen.HomeGraph.route
        }
    }

    // NavHost: The root of the navigation structure
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        // 2. Authentication Flow Graph
        authNavGraph(navController)

        // 3. Main App Flow Graph
        homeNavGraph(navController)

        // 4. Admin Dashboard
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen()
        }
    }
}

// --- 4. NESTED AUTHENTICATION GRAPH DEFINITION ---

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.AuthGraph.RoleSelection.route,
        route = Screen.AuthGraph.route
    ) {
        composable(Screen.AuthGraph.RoleSelection.route) {
            RoleSelectionScreen(navController)
        }

        // CORRECTED ROUTE: Defines the argument needed for the Sign-Up screen
        composable(
            route = Screen.AuthGraph.SignUp.route + "/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            // Passes the extracted role argument to the Sign-Up Composable
            SignUpScreen(
                navController = navController,
                role = backStackEntry.arguments?.getString("role")
            )
        }

        // Login Screen
        composable(Screen.AuthGraph.Login.route) {
            LoginScreen(navController)
        }
    }
}

// --- 5. NESTED HOME (MAIN APP) GRAPH DEFINITION ---

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.HomeGraph.Dashboard.route,
        route = Screen.HomeGraph.route
    ) {
        // Main Tab Screens
        composable(Screen.HomeGraph.Dashboard.route) { /* Dashboard UI */ }
        composable(Screen.HomeGraph.Roadmaps.route) { /* Roadmap List UI */ }
        composable(Screen.HomeGraph.Mentors.route) { /* Mentor Directory UI */ }
        composable(Screen.HomeGraph.Profile.route) { /* Profile Edit/View UI */ }

        // Detail Screens
        composable(Screen.HomeGraph.RoadmapDetail.route) { /* Roadmap Detail UI */ }
        composable(Screen.HomeGraph.Chat.route) { /* Chat Screen UI */ }
    }
}

// --- 6. PLACEHOLDER UI FUNCTIONS ---
// (These need to be implemented in their own files, but are here for compilation)

@Composable fun AdminDashboardScreen() { /* UI */ }