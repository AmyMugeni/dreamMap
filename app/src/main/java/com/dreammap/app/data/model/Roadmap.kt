package com.dreammap.app.data.model

import com.google.firebase.firestore.DocumentId

// Data class for the Milestone structure within the Roadmap
data class Milestone(
    val title: String = "",
    val description: String = "",
    val estimatedDays: Int = 0,
    val tasks: List<String> = emptyList(), // Specific steps to achieve the milestone
    val isCompleted: Boolean = false // Track user progress
)

// Re-defining Roadmap with the updated structure:
data class Roadmap(
    @DocumentId val id: String = "", // Used for querying details
    val title: String = "",
    val shortDescription: String = "",
    val weeklyTimeCommitment: String = "",
    val recommendedInterests: List<String> = emptyList(),
    val skillsRequired: List<String> = emptyList(),
    val milestones: List<Milestone> = emptyList()
)

// Sample Detail Data (Using the structure for demonstration)
fun getSampleRoadmap(id: String): Roadmap {
    return Roadmap(
        id = id,
        title = "Frontend Dev Mastery",
        shortDescription = "Learn React, Vue, and modern component architecture.",
        weeklyTimeCommitment = "5 hours",
        recommendedInterests = listOf("UI/UX", "Logic", "Design"),
        skillsRequired = listOf("JavaScript", "HTML/CSS", "Git"),
        milestones = listOf(
            Milestone("Setup & Foundations", "Install Node, Git, and set up your first static site.", 5, listOf("Install VS Code", "Learn basic HTML tags", "Push to GitHub"), true),
            Milestone("Component Architecture", "Master React functional components and state management.", 15, listOf("Understand hooks", "Build a small portfolio site", "Use conditional rendering"), false),
            Milestone("State Management", "Deep dive into Redux or Context API.", 10, listOf("Set up a global store", "Refactor portfolio site"), false)
        )
    )
}