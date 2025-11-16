package com.dreammap.app.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.Timestamp

/**
 * Data model for Events & Webinars associated with a roadmap.
 * Similar structure to how recommendedInterests are stored in Roadmap.
 */
data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @PropertyName("event_date") val eventDate: Timestamp? = null,
    @PropertyName("registration_url") val registrationUrl: String = "",
    @PropertyName("event_type") val eventType: String = "", // "webinar", "workshop", "conference", "networking"
    @PropertyName("is_registered") var isRegistered: Boolean = false,
    val organizer: String = "",
    val location: String = "", // "Online" or physical location
    @PropertyName("max_participants") val maxParticipants: Int = 0,
    @PropertyName("current_participants") val currentParticipants: Int = 0
)

