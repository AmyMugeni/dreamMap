package com.dreammap.app.util.constants

object FirebaseConstants {

    // --- 1. Top-Level Collections ---

    const val USERS_COLLECTION = "users"
    const val ROADMAPS_COLLECTION = "roadmaps"
    const val BOOKINGS_COLLECTION = "bookings"
    const val CHATS_COLLECTION = "chats" // Needed for the MVP chat feature
    const val MENTORSHIP_REQUESTS_COLLECTION = "mentorshipRequests" // Added for mentor management
    const val MENTEE_PROFILES_COLLECTION = "menteeProfiles" // Added for mentor management


    // --- 2. USERS Collection Field Names ---

    // Roles (used for the 'role' field)
    const val ROLE_STUDENT = "student"
    const val ROLE_MENTOR = "mentor"
    const val ROLE_ADMIN = "admin"

    // Field keys
    const val FIELD_ROLE = "role"
    const val FIELD_INTERESTS = "interests"
    const val FIELD_EXPERTISE = "expertise"
    const val FIELD_IS_AVAILABLE = "isAvailable" // Mentor filter


    // --- 3. ROADMAPS Collection Field Names ---

    const val FIELD_RECOMMENDED_INTERESTS = "recommendedInterests" // Crucial for quiz query

    // Sub-collection name
    const val UNIVERSITIES_SUBCOLLECTION = "universities"


    // --- 4. BOOKINGS / REQUESTS Field Names ---

    const val FIELD_STUDENT_ID = "studentId"
    const val FIELD_MENTOR_ID = "mentorId"
    const val FIELD_DATE_TIME = "dateTime"
    const val FIELD_STATUS = "status"


    // --- 5. CHAT Sub-Collections ---

    const val MESSAGES_SUBCOLLECTION = "messages"

    // --- 6. Status Values (Bookings and Mentorship Requests) ---
    const val STATUS_PENDING = "Pending"
    const val STATUS_ACCEPTED = "Accepted"
    const val STATUS_DECLINED = "Declined"
}