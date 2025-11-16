package com.dreammap.app.data.model

import com.google.firebase.firestore.PropertyName

/**
 * Data model for Resources (articles, videos) associated with a roadmap.
 * Similar structure to how recommendedInterests are stored in Roadmap.
 */
data class Resource(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @PropertyName("resource_type") val resourceType: String = "", // "article", "video", "course", "tutorial"
    val url: String = "",
    @PropertyName("thumbnail_url") val thumbnailUrl: String? = null,
    @PropertyName("duration_minutes") val durationMinutes: Int = 0, // For videos/courses
    val author: String = "",
    @PropertyName("is_bookmarked") var isBookmarked: Boolean = false,
    @PropertyName("difficulty_level") val difficultyLevel: String = "" // "beginner", "intermediate", "advanced"
)

