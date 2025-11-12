
package com.dreammap.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp

// 1. Mentorship Request Model (Used when a student asks to be mentored)
data class MentorshipRequest(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val mentorId: String = "",
    val mentorName: String = "",
    val targetRoadmap: String = "", // The roadmap the student is focusing on
    val motivationMessage: String = "", // Student's message to the mentor
    val status: String = "Pending", // "Pending", "Accepted", "Declined"
    val dateRequested: Timestamp? = null
)

// 2. Mentee Profile Model (Used for the mentor's list of active students)
data class MenteeProfile(
    val id: String = "",
    val name: String = "",
    val currentRoadmap: String = "",   // use currentRoadmap instead of roadmapTitle
    val completedGoals: Int = 0,
    val totalGoals: Int = 0
) {
    val progressPercent: Float
        get() = if (totalGoals > 0) (completedGoals.toFloat() / totalGoals) * 100f else 0f
}

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now()
)