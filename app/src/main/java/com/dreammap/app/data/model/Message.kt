package com.dreammap.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Message(
    @DocumentId
    val id: String = "",
    val senderId: String = "",  // UID of the sender (student or mentor)
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(), // Crucial for ordering messages
    val isRead: Boolean = false
)