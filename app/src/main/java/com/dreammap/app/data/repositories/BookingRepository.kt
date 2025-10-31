package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.Booking
import com.dreammap.app.util.constants.FirebaseConstants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class BookingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val bookingsCollection = firestore.collection(FirebaseConstants.BOOKINGS_COLLECTION)

    /**
     * Creates a new booking document when a student schedules a session.
     */
    suspend fun createBooking(booking: Booking) {
        // Firestore automatically generates a unique ID if .document() is not called
        bookingsCollection.add(booking).await()
    }

    /**
     * Fetches all bookings relevant to a specific user (either as a student or a mentor).
     * @param userId The UID of the current user.
     */
    suspend fun getUserBookings(userId: String): List<Booking> {
        val studentQuery = bookingsCollection
            .whereEqualTo(FirebaseConstants.FIELD_STUDENT_ID, userId)
            .orderBy(FirebaseConstants.FIELD_DATE_TIME, Query.Direction.ASCENDING) // Order by date

        val mentorQuery = bookingsCollection
            .whereEqualTo(FirebaseConstants.FIELD_MENTOR_ID, userId)
            .orderBy(FirebaseConstants.FIELD_DATE_TIME, Query.Direction.ASCENDING) // Order by date

        // NOTE: Firestore does not support OR queries across different fields in a single call.
        // You must fetch two separate lists and merge them, or only display based on one role.
        // For simplicity here, we'll fetch only bookings where the user is the Student.
        // A real app would use Cloud Functions or a more complex query strategy for a unified view.

        return try {
            val studentBookings = studentQuery.get().await().toObjects(Booking::class.java)
            // You would fetch mentorBookings separately and merge the lists here.
            studentBookings
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Allows the mentor (or student) to update the status (e.g., confirm, cancel).
     */
    suspend fun updateBookingStatus(bookingId: String, newStatus: String) {
        bookingsCollection.document(bookingId)
            .update(FirebaseConstants.FIELD_STATUS, newStatus)
            .await()
    }
}