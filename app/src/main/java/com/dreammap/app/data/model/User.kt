package com.dreammap.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp

/**
 * Data model representing a user document in the 'users' Firestore collection.
 * It combines common fields with role-specific fields for Students, Mentors, and Admins.
 */
data class User(
    // CORE FIELDS (Common to all roles)
    // @DocumentId ensures this field is automatically populated with the Firestore document's ID (which is the Firebase Auth UID)
    @DocumentId
    val uid: String = "",
    val role: String = "", // Used to determine UI and access (e.g., "student", "mentor", "admin")
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String? = null,
    val dateJoined: Timestamp = Timestamp.now(),

    // STUDENT FIELDS
    val quizCompleted: Boolean = false,
    val interests: List<String> = emptyList(), // For personalized career recommendations

    // MENTOR FIELDS
    val expertise: List<String> = emptyList(),
    val roadmaps: List<String> = emptyList(), // e.g., ["Software Engineering", "UX Design"]
    val bio: String? = null,
    val isAvailable: Boolean = true // Used for filtering available mentors
)

object ChatUsers {
    // These IDs should match the security rules you'd use in Firestore
    const val MENTOR_ID = "mentor_123"
    const val MENTEE_ID = "mentee_001"
}

// Enum to easily switch the app's current user perspective for testing
enum class CurrentUserRole {
    MENTOR, MENTEE
}