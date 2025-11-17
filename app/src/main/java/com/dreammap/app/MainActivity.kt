package com.dreammap.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.dreammap.app.data.repositories.*
import com.dreammap.app.screens.auth.AuthViewModel
import com.dreammap.app.screens.auth.LoginScreen
import com.dreammap.app.screens.auth.RoleSelectionScreen
import com.dreammap.app.screens.auth.SignUpScreen
import com.dreammap.app.screens.auth.SplashScreen
import com.dreammap.app.screens.admin.*
import com.dreammap.app.screens.mentor.*
import com.dreammap.app.screens.student.*
import com.dreammap.app.ui.theme.DreamMapTheme
import com.dreammap.app.util.constants.FirebaseConstants
import com.dreammap.app.viewmodels.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    // repositories (init in onCreate)
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var bookingRepository: BookingRepository
    private lateinit var mentorshipRepository: MentorshipRepository
    private lateinit var roadmapRepository: RoadmapRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        Log.d("MainActivity", "Firebase initialized")
        val firestore = FirebaseFirestore.getInstance()

        // initialize your repositories (use the constructors you already defined)
        authRepository = AuthRepository()
        userRepository = UserRepository(firestore)
        bookingRepository = BookingRepository(firestore)
        mentorship_repository_safe_holder = MentorshipRepositoryImpl(firestore, "com.dreammap.app")
        mentorship_repository = mentorship_repository_safe_holder
        roadmapRepository = RoadmapRepository(firestore)

        setContent {
            DreamMapTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavRoot(
                        authRepository = authRepository,
                        userRepository = userRepository,
                        bookingRepository = bookingRepository,
                        mentorshipRepository = mentorship_repository,
                        roadmapRepository = roadmapRepository
                    )
                }
            }
        }
    }

    // small mutable holder to avoid val name collisions in the file-scope
    companion object {
        // we use a companion mutable var to initialize before setContent (safe here)
        private lateinit var mentorship_repository_safe_holder: MentorshipRepository
        private lateinit var mentorship_repository: MentorshipRepository
    }
}

/** trivial wrapper (keeps call sites nicer) */
private fun mentorship_repository_safe(m: MentorshipRepository): MentorshipRepository = m

@Composable
fun AppNavRoot(
    authRepository: AuthRepository,
    userRepository: UserRepository,
    bookingRepository: BookingRepository,
    mentorshipRepository: MentorshipRepository,
    roadmapRepository: RoadmapRepository
) {
    val navController = rememberNavController()

    // create ViewModel factories we already have (these classes are in your viewmodels package)
    val authVmFactory = AuthViewModelFactory(authRepository)
    val mentorVmFactory = MentorViewModelFactory(userRepository, bookingRepository, mentorshipRepository)
    val mentorDirectoryVmFactory = MentorDirectoryViewModelFactory(userRepository)
    val roadmapVmFactory = RoadmapViewModelFactory(roadmapRepository)
    val adminVmFactory = AdminViewModelFactory(userRepository)
    val mentorProfileVm: MentorProfileViewModel = viewModel()


    // Create AuthViewModel at root so many screens can read currentUser and isLoading
    val authViewModel: AuthViewModel = viewModel(factory = authVmFactory)

    // track whether we've already auto-navigated after auth change to avoid loops
    var didAutoNavigateForAuth by remember { mutableStateOf(false) }

    // Observe auth state
    val currentUser by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // When user becomes non-null, reset flag so LaunchedEffect below can run correctly
    LaunchedEffect(currentUser) {
        if (currentUser != null) didAutoNavigateForAuth = false
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // ---- SPLASH ----
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // ---- AUTH FLOW ----
        composable(Screen.AuthGraph.RoleSelection.route) {
            RoleSelectionScreen(navController)
        }

        // Login — your most recent LoginScreen signature expects (NavController, AuthRepository)
        composable(Screen.AuthGraph.Login.route) {
            LoginScreen(
                navController = navController,
                authRepository = authRepository
            )
        }

        // SignUp with role arg — your SignUpScreen expects (NavController, AuthViewModel, role)
        composable(
            route = Screen.AuthGraph.SignUp.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backEntry ->
            val role = backEntry.arguments?.getString("role") ?: "student"
            SignUpScreen(navController = navController, authViewModel = authViewModel, role = role)
        }

        // Career Interest Quiz
        composable(Screen.AuthGraph.CareerQuiz.route) {
            com.dreammap.app.screens.student.CareerInterestQuizScreen(
                navController = navController,
                authViewModel = authViewModel,
                userRepository = userRepository,
                roadmapRepository = roadmapRepository
            )
        }

        // ---- STUDENT FLOW (prefixed by home_graph) ----

        // Dashboard
        composable("${Screen.HomeGraph.route}/${Screen.HomeGraph.Dashboard.route}") {
            DashboardScreen(navController = navController, authViewModel = authViewModel)
        }

        // Mentors list (needs MentorDirectoryViewModel)
        composable("${Screen.HomeGraph.route}/${Screen.HomeGraph.Mentors.route}") {
            val mentorDirVM: MentorDirectoryViewModel = viewModel(factory = mentorDirectoryVmFactory)
            MentorsScreen(
                navController = navController,
                viewModel = mentorDirVM,
                onMentorClick = { mentorId ->
                    // navigate to mentor detail full path: home_graph/mentor_detail/{mentorId}
                    navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.MentorDetail.createRoute(mentorId)}")
                }
            )
        }

        // Mentor detail (student -> mentor detail)
        composable(
            route = "${Screen.HomeGraph.route}/${Screen.HomeGraph.MentorDetail.route}",
            arguments = listOf(navArgument("mentorId") { type = NavType.StringType })
        ) { backEntry ->
            val mentorId = backEntry.arguments?.getString("mentorId")

            val mentorVm: MentorViewModel = viewModel(factory = MentorViewModelFactory(userRepository, bookingRepository, mentorshipRepository))

            MentorDetailScreen(
                navController = navController,
                mentorId = mentorId,
                authViewModel = authViewModel,
                mentorViewModel = mentorVm
            )
        }


        // Roadmaps list — your RoadmapListScreen expects (NavController, RoadmapRepository)
        composable("${Screen.HomeGraph.route}/${Screen.HomeGraph.Roadmaps.route}") {
            RoadmapListScreen(navController = navController, roadmapRepository = roadmapRepository)
        }

        // Roadmap detail
        composable(
            route = "${Screen.HomeGraph.route}/${Screen.HomeGraph.RoadmapDetail.route}",
            arguments = listOf(navArgument("roadmapId") { type = NavType.StringType })
        ) { backEntry ->
            val roadmapId = backEntry.arguments?.getString("roadmapId") ?: ""
            val roadmapVm: RoadmapViewModel = viewModel(factory = roadmapVmFactory)
            RoadmapDetailScreen(navController = navController, roadmapId = roadmapId, roadmapViewModel = roadmapVm)
        }

        // Profile Screen
        composable("${Screen.HomeGraph.route}/${Screen.HomeGraph.Profile.route}") {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        // Quiz Screen
        composable("${Screen.HomeGraph.route}/${Screen.HomeGraph.Quiz.route}") {
            QuizScreen(navController = navController)
        }

        // Quiz Results Screen
        composable("${Screen.HomeGraph.route}/${Screen.HomeGraph.QuizResults.route}") {
            val careerVm: com.dreammap.app.screens.career.CareerViewModel = viewModel(
                factory = com.dreammap.app.screens.career.CareerViewModelFactory(roadmapRepository)
            )
            QuizResultsScreen(
                navController = navController,
                authViewModel = authViewModel,
                userRepository = userRepository,
                careerViewModel = careerVm
            )
        }

        // Chat with optional query param partnerId
        composable(
            route = "${Screen.HomeGraph.route}/chat?partnerId={partnerId}",
            arguments = listOf(navArgument("partnerId") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) { backEntry ->
            val partnerId = backEntry.arguments?.getString("partnerId")
            ChatScreen(navController = navController, partnerId = partnerId, authViewModel = authViewModel)
        }

        // ---- MENTOR FLOW ----
        // Mentor root (expects two path args)
        // -------------------- MENTOR GRAPH PARENT ---------------------
        composable(
            route = Screen.MentorGraph.route,
            arguments = listOf(
                navArgument(Screen.MentorGraph.KEY_USER_ID) { type = NavType.StringType },
                navArgument(Screen.MentorGraph.KEY_USER_NAME) { type = NavType.StringType }
            )
        ) { backEntry ->
            val mentorId = backEntry.arguments?.getString(Screen.MentorGraph.KEY_USER_ID) ?: ""
            val mentorName = backEntry.arguments?.getString(Screen.MentorGraph.KEY_USER_NAME) ?: "Mentor"
            val mentorVm: MentorViewModel = viewModel(factory = mentorVmFactory)

            MentorDashboardScreen(navController, mentorVm, mentorId, mentorName)
        }

        composable(
            route = "${Screen.MentorGraph.route}/manage_mentees",
            arguments = listOf(
                navArgument(Screen.MentorGraph.KEY_USER_ID) { type = NavType.StringType },
                navArgument(Screen.MentorGraph.KEY_USER_NAME) { type = NavType.StringType }
            )
        ) {
            val mentorVm: MentorViewModel = viewModel(factory = mentorVmFactory)
            ActiveMenteesScreen(navController, mentorVm)
        }

        composable(
            route = "${Screen.MentorGraph.route}/mentor_requests",
            arguments = listOf(
                navArgument(Screen.MentorGraph.KEY_USER_ID) { type = NavType.StringType },
                navArgument(Screen.MentorGraph.KEY_USER_NAME) { type = NavType.StringType }
            )
        ) {
            val mentorVm: MentorViewModel = viewModel(factory = mentorVmFactory)
            MentorRequestScreen(navController, mentorVm)
        }

        composable(
            route = "${Screen.MentorGraph.route}/mentee_detail/{menteeId}",
            arguments = listOf(
                navArgument(Screen.MentorGraph.KEY_USER_ID) { type = NavType.StringType },
                navArgument(Screen.MentorGraph.KEY_USER_NAME) { type = NavType.StringType },
                navArgument("menteeId") { type = NavType.StringType }
            )
        ) { backEntry ->
            val menteeId = backEntry.arguments?.getString("menteeId") ?: ""
            val mentorVm: MentorViewModel = viewModel(factory = mentorVmFactory)
            MenteeDetailScreen(navController, menteeId, mentorVm)
        }

        composable(
            route = "${Screen.MentorGraph.route}/profile/mentorEdit/{mentorId}",
            arguments = listOf(
                navArgument(Screen.MentorGraph.KEY_USER_ID) { type = NavType.StringType },
                navArgument(Screen.MentorGraph.KEY_USER_NAME) { type = NavType.StringType },
                navArgument("mentorId") { type = NavType.StringType }
            )
        ) { backEntry ->
            val mentorUid = backEntry.arguments?.getString("mentorId") ?: ""
            val mentorProfileVm: MentorProfileViewModel = viewModel() // default constructor
            MentorProfileEditScreen(
                mentorUid = mentorUid,
                viewModel = mentorProfileVm,
                navController = navController,
                authViewModel = authViewModel
            ) {
                navController.popBackStack()
            }
        }

        // ---- ADMIN FLOW ----
        // Admin Dashboard
        composable("${Screen.AdminGraph.route}/${Screen.AdminGraph.Dashboard.route}") {
            val adminVm: AdminViewModel = viewModel(factory = adminVmFactory)
            AdminDashboardScreen(
                navController = navController,
                adminViewModel = adminVm,
                authViewModel = authViewModel
            )
        }

        // Admin Profile
        composable("${Screen.AdminGraph.route}/${Screen.AdminGraph.Profile.route}") {
            AdminProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                userRepository = userRepository
            )
        }

        // Manage Students
        composable("${Screen.AdminGraph.route}/${Screen.AdminGraph.ManageStudents.route}") {
            val adminVm: AdminViewModel = viewModel(factory = adminVmFactory)
            ManageStudentsScreen(navController = navController, adminViewModel = adminVm)
        }

        // Manage Mentors
        composable("${Screen.AdminGraph.route}/${Screen.AdminGraph.ManageMentors.route}") {
            val adminVm: AdminViewModel = viewModel(factory = adminVmFactory)
            ManageMentorsScreen(navController = navController, adminViewModel = adminVm)
        }

        // User Detail
        composable(
            route = "${Screen.AdminGraph.route}/${Screen.AdminGraph.UserDetail.route}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backEntry ->
            val userId = backEntry.arguments?.getString("userId") ?: ""
            val adminVm: AdminViewModel = viewModel(factory = adminVmFactory)
            UserDetailScreen(navController = navController, userId = userId, adminViewModel = adminVm)
        }
    }
    // observe auth state to redirect immediately after successful login/signup
    LaunchedEffect(currentUser) {
        if (currentUser != null && !didAutoNavigateForAuth) {
            didAutoNavigateForAuth = true
            when (currentUser?.role) {
                FirebaseConstants.ROLE_MENTOR -> {
                    navController.navigate(Screen.MentorGraph.createRoute(currentUser!!.uid, currentUser!!.name)) {
                        popUpTo(Screen.AuthGraph.route) { inclusive = true }
                    }
                }
                FirebaseConstants.ROLE_ADMIN -> {
                    navController.navigate("${Screen.AdminGraph.route}/${Screen.AdminGraph.Dashboard.route}") {
                        popUpTo(Screen.AuthGraph.route) { inclusive = true }
                    }
                }
                else -> {
                    // For students, check if they've completed the quiz
                    if (currentUser?.quizCompleted == false) {
                        navController.navigate(Screen.AuthGraph.CareerQuiz.route) {
                            popUpTo(Screen.AuthGraph.route) { inclusive = false }
                        }
                    } else {
                        navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.Dashboard.route}") {
                            popUpTo(Screen.AuthGraph.route) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
