package com.dreammap.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

// Data class for the Milestone structure within the Roadmap
data class Milestone(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @PropertyName("estimated_days") var estimatedDays: Int = 0, // Change to var
    val tasks: List<String> = emptyList(),
    @PropertyName("is_completed") var isCompleted: Boolean = false
)


// The main Roadmap document structure
data class Roadmap(
    @DocumentId val id: String = "",
    val title: String = "",
    @PropertyName("short_description") var shortDescription: String = "", // Change to var
    @PropertyName("weekly_time_commitment") var weeklyTimeCommitment: String = "", // Change to var
    @PropertyName("recommended_interests") var recommendedInterests: List<String> = emptyList(), // Change to var
    @PropertyName("skills_required") var skillsRequired: List<String> = emptyList(), // Change to var
    var milestones: List<Milestone> = emptyList()
)
