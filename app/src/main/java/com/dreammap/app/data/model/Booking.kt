package com.dreammap.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp

/**
 * Data model for a mentorship session booking document in the 'bookings' Firestore collection.
 */
data class Booking(
    @DocumentId
    val id: String = "",
    val studentId: String = "", // UID of the student who requested the session
    val mentorId: String = "",   // UID of the mentor being booked
    val dateTime: Timestamp = Timestamp.now(), // Scheduled date and time of the session
    val topic: String = "",     // Brief subject of the discussion
    val status: String = "pending", // Status: "pending", "confirmed", "completed", "cancelled"
    val studentName: String = "", // Cached name for easier display on the mentor side
    val mentorName: String = ""   // Cached name for easier display on the student side
)
