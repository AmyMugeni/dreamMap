package com.dreammap.app.data.model

import com.google.firebase.firestore.DocumentId

// Data class for the Milestone structure within the Roadmap
data class Milestone(
    val stepTitle: String = "",
    val details: String = ""
)

// Main Roadmap Data Class
data class Roadmap(
    @DocumentId val id: String = "", // Used for querying details
    val title: String = "",
    val shortDescription: String = "",
    val averageCostEstimate: Double = 0.0,
    val recommendedInterests: List<String> = emptyList(), // Crucial for the quiz
    val skillsRequired: List<String> = emptyList(),
    val milestones: List<Milestone> = emptyList()
)