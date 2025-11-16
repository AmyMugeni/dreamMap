package com.dreammap.app

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Defines all navigation routes (screens) in the application.
 * Routes are grouped into AuthGraph, HomeGraph (Student), and MentorGraph.
 */
sealed class Screen(val route: String) {
    // 1. Initial State
    object Splash : Screen("splash")

    // 2. Authentication Flow (The Auth Graph)
    object AuthGraph : Screen("auth_graph") {
        // Nested destinations within the Auth Graph
        object RoleSelection : Screen("role_selection")
        object SignUp : Screen("sign_up/{role}") {
            fun createRoute(role: String) = "sign_up/$role"
        }
        object Login : Screen("login")
    }


    // 3. Main App Flow (The Home Graph - Student Role)
    object HomeGraph : Screen("home_graph") {
        // Primary nested destinations (e.g., Tabs)
        object Dashboard : Screen("dashboard")
        object Roadmaps : Screen("roadmaps")
        object Mentors : Screen("mentors")
        object Profile : Screen("profile")

        // Detailed destinations with arguments
        object RoadmapDetail : Screen("roadmap_detail/{roadmapId}") {
            fun createRoute(roadmapId: String) = "roadmap_detail/$roadmapId"
        }

        object MentorDetail : Screen("mentor_detail/{mentorId}") {
            fun createRoute(mentorId: String) = "mentor_detail/$mentorId"
        }
        // Using query parameter for optionality
        object Chat : Screen("chat?partnerId={partnerId}") {
            fun createRoute(partnerId: String?) = partnerId?.let { "chat?partnerId=$it" } ?: "chat"
        }
    }

    // 4. Mentor App Flow (The Mentor Graph)
    object MentorGraph : Screen("mentor_graph/{userId}/{userName}") {
        fun createRoute(userId: String, userName: String) = "mentor_graph/$userId/$userName"
        const val KEY_USER_ID = "userId"
        const val KEY_USER_NAME = "userName"

        object Dashboard : Screen("mentor_dashboard")
        object ManageMentees : Screen("manage_mentees")
        object MentorRequests : Screen("mentor_requests")
        object ProfileEdit : Screen("profile/mentorEdit/{mentorId}") {
            fun createRoute(mentorId: String) = "profile/mentorEdit/$mentorId"
        }
        object MenteeDetail : Screen("mentee_detail/{menteeId}") {
            fun createRoute(menteeId: String) = "mentee_detail/$menteeId"
        }
    }


    // 5. Admin App Flow (The Admin Graph)
    object AdminGraph : Screen("admin_graph") {
        object Dashboard : Screen("dashboard")
        object ManageStudents : Screen("manage_students")
        object ManageMentors : Screen("manage_mentors")
        object UserDetail : Screen("user_detail/{userId}") {
            fun createRoute(userId: String) = "user_detail/$userId"
        }
    }
}