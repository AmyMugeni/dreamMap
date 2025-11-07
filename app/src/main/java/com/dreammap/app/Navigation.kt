package com.dreammap.app

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Defines all navigation routes (screens) in the application.
 * Routes are grouped into AuthGraph, HomeGraph, etc.
 */
sealed class Screen(val route: String) {
    // 1. Initial State
    object Splash : Screen("splash_screen")

    // 2. Authentication Flow (The Auth Graph)
    object AuthGraph : Screen("auth_graph") {
        // Nested destinations within the Auth Graph
        object RoleSelection : Screen("auth/role_select")
        object SignUp : Screen("auth/signup/{role}") {
            fun createRoute(role: String) = "auth/signup/$role"
        }
        object Login : Screen("auth/login")
    }

    // 3. Main App Flow (The Home Graph)
    object HomeGraph : Screen("home_graph") {
        // Nested destinations within the Home Graph (e.g., Tabs)
        object Dashboard : Screen("home/dashboard")
        object Mentors : Screen("home/mentors")
        object Roadmaps : Screen("home/roadmaps")
        object Profile : Screen("home/profile")

        // Detailed destinations with arguments
        object RoadmapDetail : Screen("home/roadmap/{roadmapId}") {
            fun createRoute(roadmapId: String) = "home/roadmap/$roadmapId"
        }
        object MentorDetail : Screen("home/mentor/{mentorId}") {
            fun createRoute(mentorId: String) = "home/mentor/$mentorId"
        }
        object Chat : Screen("home/chat/{partnerId}") {
            fun createRoute(partnerId: String) = "home/chat/$partnerId"
        }
    }

    // 4. Admin Flow
    object AdminDashboard : Screen("admin_dashboard")
}