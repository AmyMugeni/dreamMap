package com.dreammap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.google.firebase.FirebaseApp
import android.util.Log

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
        // ✅ Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseTest", "Firebase initialized successfully!")

        // ✅ Create repositories
        val userRepository = UserRepository()
        authRepository = AuthRepository(userRepository = userRepository)

        // ✅ Set content
        setContent {
            DreamMapTheme {
                DreamMapNavRoot(
                    authViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
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
    Log.d("DreamMapNavRoot", "Building NavRoot...")

    val user by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    val navController = rememberNavController()

    // ✅ Use Splash as start while loading
    val startDestination = remember(user, isLoading) {
        when {
            isLoading -> Screen.Splash.route
            user == null -> Screen.AuthGraph.route
            user?.role == "admin" -> Screen.AdminDashboard.route
            else -> Screen.HomeGraph.route
        }
    }

    // Always start from Splash
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(navController, authViewModel)
        }


        // Authentication Flow
        authNavGraph(navController, authViewModel)

        // Home/Main Flow
        homeNavGraph(navController, authViewModel)

        // Admin Dashboard
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen()
        }
    }
}

// --- 4. NESTED AUTHENTICATION GRAPH ---
fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = Screen.AuthGraph.RoleSelection.route,
        route = Screen.AuthGraph.route
    ) {
        // Role selection
        composable(Screen.AuthGraph.RoleSelection.route) {
            RoleSelectionScreen(navController)
        }

        // Sign-up screen with argument placeholder
        composable(
            route = Screen.AuthGraph.SignUp.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            SignUpScreen(navController, authViewModel, role)
        }

        // Login screen
        composable(Screen.AuthGraph.Login.route) {
            LoginScreen(navController, authViewModel)
        }
    }
}


// --- 5. NESTED HOME GRAPH ---
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = Screen.HomeGraph.Dashboard.route,
        route = Screen.HomeGraph.route
    ) {
        composable(Screen.HomeGraph.Dashboard.route) { /* Dashboard UI */ }
        composable(Screen.HomeGraph.Roadmaps.route) { /* Roadmap List UI */ }
        composable(Screen.HomeGraph.Mentors.route) { /* Mentor Directory UI */ }
        composable(Screen.HomeGraph.Profile.route) { /* Profile UI */ }

        composable(Screen.HomeGraph.RoadmapDetail.route) { /* Roadmap Detail UI */ }
        composable(Screen.HomeGraph.Chat.route) { /* Chat Screen UI */ }
    }
}

// --- 6. PLACEHOLDER COMPOSABLES ---
@Composable fun AdminDashboardScreen() { /* Admin Dashboard UI */ }
//@Composable fun RoleSelectionScreen(navController: NavHostController, authViewModel: AuthViewModel) { /* Role selection UI */ }
