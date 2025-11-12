package com.dreammap.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.dreammap.app.data.repositories.*
import com.dreammap.app.screens.auth.*
import com.dreammap.app.screens.mentor.*
import com.dreammap.app.screens.student.*
import com.dreammap.app.ui.theme.DreamMapTheme
import com.dreammap.app.viewmodels.MentorViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

// --- VIEWMODEL FACTORIES ---
class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MentorViewModelFactory(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val mentorshipRepository: MentorshipRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MentorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MentorViewModel(userRepository, bookingRepository, mentorshipRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// --- MAIN ACTIVITY ---
class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var bookingRepository: BookingRepository
    private lateinit var mentorshipRepository: MentorshipRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseTest", "Firebase initialized successfully!")

        // Create repositories
        userRepository = UserRepository()
        bookingRepository = BookingRepository()
        mentorshipRepository = MentorshipRepositoryImpl(FirebaseFirestore.getInstance(), "com.dreammap.app")
        authRepository = AuthRepository(userRepository = userRepository)

        setContent {
            DreamMapTheme {
                DreamMapNavRoot(
                    authViewModel = viewModel(factory = AuthViewModelFactory(authRepository)),
                    userRepository = userRepository,
                    bookingRepository = bookingRepository,
                    mentorshipRepository = mentorshipRepository
                )
            }
        }
    }
}

// --- TOP-LEVEL NAVIGATION ROOT ---
@Composable
fun DreamMapNavRoot(
    authViewModel: AuthViewModel,
    userRepository: UserRepository,
    bookingRepository: BookingRepository,
    mentorshipRepository: MentorshipRepository
) {
    val user by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val navController = rememberNavController()

    val startDestination = remember(user) {
        when {
            user == null -> Screen.AuthGraph.route
            user?.role == "admin" -> Screen.AdminDashboard.route
            user?.role == "mentor" -> Screen.MentorGraph.route
            else -> Screen.HomeGraph.route
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        // Splash Screen
        composable(Screen.Splash.route) {
            LaunchedEffect(isLoading, user) {
                if (!isLoading) {
                    navController.navigate(startDestination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // Auth Flow
        authNavGraph(navController, authViewModel)

        // Student Flow
        homeNavGraph(navController, authViewModel, userRepository, bookingRepository, mentorshipRepository)

        // Mentor Flow
        val mentorViewModelFactory = MentorViewModelFactory(userRepository, bookingRepository, mentorshipRepository)
        mentorNavGraph(navController, mentorViewModelFactory)

        // Admin Dashboard
        composable(Screen.AdminDashboard.route) { AdminDashboardScreen() }
    }
}

// --- AUTH NAV GRAPH ---
fun NavGraphBuilder.authNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    navigation(
        startDestination = Screen.AuthGraph.RoleSelection.route,
        route = Screen.AuthGraph.route
    ) {
        composable(Screen.AuthGraph.RoleSelection.route) { RoleSelectionScreen(navController) }
        composable(
            route = Screen.AuthGraph.SignUp.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            SignUpScreen(navController, authViewModel, role)
        }
        composable(Screen.AuthGraph.Login.route) { LoginScreen(navController, authViewModel) }
    }
}

// --- HOME NAV GRAPH (STUDENT FLOW) ---
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userRepository: UserRepository,
    bookingRepository: BookingRepository,
    mentorshipRepository: MentorshipRepository
) {
    navigation(
        startDestination = Screen.HomeGraph.Dashboard.route,
        route = Screen.HomeGraph.route
    ) {
        val mentorViewModelFactory = MentorViewModelFactory(userRepository, bookingRepository, mentorshipRepository)

        composable(Screen.HomeGraph.Dashboard.route) { DashboardScreen(navController, authViewModel) }
        composable(Screen.HomeGraph.Roadmaps.route) { RoadmapScreen(navController, authViewModel) }
        composable(Screen.HomeGraph.Mentors.route) { MentorsScreen(navController, authViewModel) }
        composable(Screen.HomeGraph.Profile.route) { /* Profile UI */ }
        composable(
            route = Screen.HomeGraph.MentorDetail.route,
            arguments = listOf(navArgument("mentorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mentorId = backStackEntry.arguments?.getString("mentorId")
            MentorDetailScreen(
                navController = navController,
                mentorId = mentorId,
                authViewModel = authViewModel,
                mentorViewModel = viewModel(factory = mentorViewModelFactory)
            )
        }
        composable(
            route = Screen.HomeGraph.Chat.route,
            arguments = listOf(navArgument("partnerId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val partnerId = backStackEntry.arguments?.getString("partnerId")
            ChatScreen(navController, partnerId, authViewModel)
        }
    }
}

// --- MENTOR NAV GRAPH ---
fun NavGraphBuilder.mentorNavGraph(
    navController: NavHostController,
    mentorViewModelFactory: MentorViewModelFactory
) {
    navigation(
        startDestination = "mentor/dashboard",
        route = "mentor_graph"
    ) {
        composable("mentor/dashboard") {
            val mentorViewModel: MentorViewModel = viewModel(factory = mentorViewModelFactory)
            MentorDashboardScreen(navController = navController, mentorViewModel = mentorViewModel)
        }

        composable(
            route = "mentor/menteeDetail/{menteeId}",
            arguments = listOf(navArgument("menteeId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Safe argument extraction
            val menteeId = backStackEntry.arguments?.getString("menteeId") ?: ""
            val mentorViewModel: MentorViewModel = viewModel(factory = mentorViewModelFactory)

            MenteeDetailScreen(
                navController = navController,
                menteeId = menteeId,
                mentorViewModel = mentorViewModel
            )
        }
//
//        composable("mentor/requestDetail/{requestId}") { backStackEntry ->
//            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
//            val mentorViewModel: MentorViewModel = viewModel(factory = mentorViewModelFactory)
//            MentorRequestDetailScreen(navController, requestId, mentorViewModel)
//        }
    }
}

// --- PLACEHOLDER COMPOSABLES ---
@Composable fun AdminDashboardScreen() { /* Admin Dashboard UI */ }
