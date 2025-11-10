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
import com.dreammap.app.screens.mentor.ManageMenteesScreen
import com.dreammap.app.screens.mentor.MenteeDetailScreen
import com.dreammap.app.screens.mentor.MentorDashboardScreen
import com.dreammap.app.screens.student.ChatScreen
import com.dreammap.app.screens.student.DashboardScreen
import com.dreammap.app.screens.student.MentorDetailScreen
import com.dreammap.app.screens.student.MentorsScreen
import com.dreammap.app.screens.student.RoadmapScreen
import com.dreammap.app.ui.theme.DreamMapTheme
import com.google.firebase.FirebaseApp
import android.util.Log

// --- 0. NAVIGATION ROUTES ---
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

    // ✅ Determine the correct start destination based on auth state and role
    val startDestination = remember(user, isLoading) {
        when {
            isLoading -> Screen.Splash.route
            user == null -> Screen.AuthGraph.route
            user?.role == "admin" -> Screen.AdminDashboard.route
            user?.role == "mentor" -> Screen.MentorGraph.route // Route to Mentor graph
            else -> Screen.HomeGraph.route // Default for students
        }
    }

    // NavHost determines the graph to show after the splash screen
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route // Always start at Splash, which will then navigate
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(navController, authViewModel)
        }

        // Authentication Flow
        authNavGraph(navController, authViewModel)

        // Main Flow (for students)
        homeNavGraph(navController, authViewModel)

        // Mentor Flow
        mentorNavGraph(navController, authViewModel)

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
        composable(Screen.AuthGraph.RoleSelection.route) {
            RoleSelectionScreen(navController)
        }
        composable(
            route = Screen.AuthGraph.SignUp.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            SignUpScreen(navController, authViewModel, role)
        }
        composable(Screen.AuthGraph.Login.route) {
            LoginScreen(navController, authViewModel)
        }
    }
}


// --- 5. NESTED HOME GRAPH (FOR STUDENTS) ---
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = Screen.HomeGraph.Dashboard.route,
        route = Screen.HomeGraph.route
    ) {
        composable(Screen.HomeGraph.Dashboard.route) { DashboardScreen(navController, authViewModel) }
        composable(Screen.HomeGraph.Roadmaps.route) { RoadmapScreen(navController, authViewModel) }
        composable(Screen.HomeGraph.Mentors.route) { MentorsScreen(navController, authViewModel) }
        composable(Screen.HomeGraph.Profile.route) { /* Profile UI */ }

        composable(
            route = Screen.HomeGraph.RoadmapDetail.route,
            arguments = listOf(navArgument("roadmapId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId")
            // RoadmapDetailScreen(roadmapId, navController)
        }
        composable(
            route = Screen.HomeGraph.MentorDetail.route,
            arguments = listOf(navArgument("mentorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mentorId = backStackEntry.arguments?.getString("mentorId")
            MentorDetailScreen(navController, mentorId)
        }
        composable(
            route = Screen.HomeGraph.Chat.route,
            arguments = listOf(navArgument("partnerId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val partnerId = backStackEntry.arguments?.getString("partnerId")
            ChatScreen(navController, partnerId, authViewModel)
        }
    }
}

// --- 6. NESTED MENTOR GRAPH ---
fun NavGraphBuilder.mentorNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = Screen.MentorGraph.Dashboard.route,
        route = Screen.MentorGraph.route
    ) {
        composable(Screen.MentorGraph.Dashboard.route) {
            MentorDashboardScreen(navController, authViewModel)
        }
        composable(Screen.MentorGraph.ManageMentees.route) {
            ManageMenteesScreen(navController = navController)
        }
        composable(
            route = Screen.MentorGraph.MenteeDetail.route,
            arguments = listOf(navArgument("menteeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val menteeId = backStackEntry.arguments?.getString("menteeId")
            MenteeDetailScreen(navController, menteeId)
        }
    }
}


// --- 7. PLACEHOLDER COMPOSABLES ---
@Composable fun AdminDashboardScreen() { /* Admin Dashboard UI */ }