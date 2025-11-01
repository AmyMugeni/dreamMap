package com.dreammap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

// --- 1. VIEWMODEL FACTORY ---
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

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userRepository = UserRepository()
        authRepository = AuthRepository(userRepository = userRepository)

        setContent {
            DreamMapTheme {
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
    // Collect the user state and loading state to drive navigation
    val user by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    val navController = rememberNavController()

    // **CRITICAL LOGIC:** Determine the true starting point based on authentication state.
    // This is the source of truth for routing and resolves the "direct child" errors.
    val startDestination = remember(user, isLoading) {
        when {
            isLoading -> Screen.Splash.route
            user == null -> Screen.AuthGraph.route
            user?.role == "admin" -> Screen.AdminDashboard.route
            else -> Screen.HomeGraph.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Splash Screen (No navigation logic inside)
        composable(Screen.Splash.route) {
            SplashScreen(navController, authViewModel)
        }

        // 2. Authentication Flow Graph
        authNavGraph(navController, authViewModel)

        // 3. Main App Flow Graph (ViewModel passed for Profile/Logout)
        homeNavGraph(navController, authViewModel)

        // 4. Admin Dashboard
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen()
        }
    }
}

// --- 4. NESTED AUTHENTICATION GRAPH DEFINITION ---
fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = Screen.AuthGraph.RoleSelection.route,
        route = Screen.AuthGraph.route
    ) {
        // Role Selection Screen
        composable(Screen.AuthGraph.RoleSelection.route) {
            RoleSelectionScreen(navController, authViewModel)
        }

        // Sign Up Screen (Route with Role Argument)
        composable(
            route = Screen.AuthGraph.SignUp.route + "/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            SignUpScreen(
                navController = navController,
                authViewModel = authViewModel,
                role = backStackEntry.arguments?.getString("role")
            )
        }

        // Login Screen
        composable(Screen.AuthGraph.Login.route) {
            LoginScreen(navController, authViewModel)
        }
    }
}

// --- 5. NESTED HOME (MAIN APP) GRAPH DEFINITION ---
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = Screen.HomeGraph.Dashboard.route,
        route = Screen.HomeGraph.route
    ) {
        // Main Tab Screens
        composable(Screen.HomeGraph.Dashboard.route) { /* Dashboard UI */ }
        composable(Screen.HomeGraph.Roadmaps.route) { /* Roadmap List UI */ }
        composable(Screen.HomeGraph.Mentors.route) { /* Mentor Directory UI */ }
        composable(Screen.HomeGraph.Profile.route) { /* Profile UI */ }

        // Detail Screens
        composable(Screen.HomeGraph.RoadmapDetail.route) { /* Roadmap Detail UI */ }
        composable(Screen.HomeGraph.Chat.route) { /* Chat Screen UI */ }
    }
}

// --- 6. PLACEHOLDER UI FUNCTIONS ---
@Composable fun AdminDashboardScreen() { /* UI */ }
@Composable fun RoleSelectionScreen(navController: NavHostController, authViewModel: AuthViewModel) { /* UI */ }
// NOTE: SplashScreen, SignUpScreen, and LoginScreen must also have correct signatures
// and be defined as top-level functions in their respective files.