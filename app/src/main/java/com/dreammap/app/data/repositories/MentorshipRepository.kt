package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.MenteeProfile
import com.dreammap.app.data.model.MentorshipRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import android.util.Log

/**
 * Interface defining the contract for managing mentorship-related data.
 */
interface MentorshipRepository {
    fun getPendingRequests(mentorId: String): Flow<List<MentorshipRequest>>
    fun getActiveMentees(mentorId: String): Flow<List<MenteeProfile>>
    suspend fun acceptMentorshipRequest(requestId: String, studentId: String, mentorId: String)
    suspend fun declineMentorshipRequest(requestId: String)
    suspend fun sendMentorshipRequest(request: MentorshipRequest)
    fun getMentorshipRequestStatus(studentId: String, mentorId: String): Flow<String?>
    fun getStudentRequestStatus(studentId: String, mentorId: String): Flow<String?>
}

/**
 * Concrete implementation of the MentorshipRepository using Firestore.
 */
class MentorshipRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val appId: String
) : MentorshipRepository {

    private val TAG = "MentorshipRepo"

    // Firestore path for mentorship requests (Public Data)
    private fun getRequestsCollection() =
        firestore.collection("artifacts").document(appId)
            .collection("public").document("data")
            .collection("mentorship_requests")

    // Firestore path for user profiles (for updating the student's mentor ID)
    private fun getUsersCollection() =
        firestore.collection("artifacts").document(appId)
            .collection("public").document("data")
            .collection("users")

    /**
     * Retrieves a real-time stream of pending requests for a specific mentor.
     */
    override fun getPendingRequests(mentorId: String): Flow<List<MentorshipRequest>> {
        return getRequestsCollection()
            .whereEqualTo("mentorId", mentorId)
            .whereEqualTo("status", "Pending")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject<MentorshipRequest>()?.copy(id = doc.id)
                }
            }
    }

    /**
     * Retrieves a real-time stream of active mentees for a specific mentor.
     */
    override fun getActiveMentees(mentorId: String): Flow<List<MenteeProfile>> {
        return getRequestsCollection()
            .whereEqualTo("mentorId", mentorId)
            .whereEqualTo("status", "Accepted")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    val request = doc.toObject<MentorshipRequest>()
                    // Convert the Accepted Request into a MenteeProfile
                    if (request != null) {
                        MenteeProfile(
                            id = request.studentId,
                            name = request.studentName,
                            currentRoadmap = request.targetRoadmap,
                            completedGoals = 2, // mock value
                            totalGoals = 4      // mock value
                        )
                    } else null
                }
            }
    }
    
    /**
     * Retrieves the status of a mentorship request from a specific student to a specific mentor.
     * Returns a Flow that emits the status as a String, or null if no request exists.
     */
    override fun getMentorshipRequestStatus(studentId: String, mentorId: String): Flow<String?> {
        return getRequestsCollection()
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("mentorId", mentorId)
            .snapshots()
            .map { snapshot ->
                // If there are multiple requests, this logic takes the first one.
                // You might want to order by date to get the most recent one.
                snapshot.documents.firstOrNull()?.getString("status")
            }
    }
    
    /**
     * Alias for getMentorshipRequestStatus to resolve ViewModel error.
     */
    override fun getStudentRequestStatus(studentId: String, mentorId: String): Flow<String?> {
        return getMentorshipRequestStatus(studentId, mentorId)
    }

    /**
     * Executes a transaction to accept a request:
     * 1. Updates the request status to "Accepted".
     * 2. Sets the student's `currentMentorId` field in their User profile.
     */
    override suspend fun acceptMentorshipRequest(requestId: String, studentId: String, mentorId: String) {
        val requestRef = getRequestsCollection().document(requestId)
        val studentRef = getUsersCollection().document(studentId)

        try {
            firestore.runTransaction { transaction ->
                // 1. Update the MentorshipRequest status
                transaction.update(requestRef, "status", "Accepted")

                // 2. Update the Student's User profile
                transaction.update(studentRef, "currentMentorId", mentorId)

                // Success
                null
            }.await()

            Log.i(TAG, "Mentorship accepted: Request $requestId, Student $studentId assigned to Mentor $mentorId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to accept mentorship request: ${e.message}", e)
            throw e
        }
    }

    /**
     * Updates the request status to "Declined".
     */
    override suspend fun declineMentorshipRequest(requestId: String) {
        try {
            getRequestsCollection().document(requestId)
                .update("status", "Declined")
                .await()
            Log.i(TAG, "Mentorship declined: Request $requestId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decline mentorship request: ${e.message}", e)
            throw e
        }
    }

    /**
     * Creates a new MentorshipRequest document (Student action).
     */
    override suspend fun sendMentorshipRequest(request: MentorshipRequest) {
        try {
            getRequestsCollection().add(request).await()
            Log.i(TAG, "Mentorship request sent from ${request.studentId} to ${request.mentorId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send mentorship request: ${e.message}", e)
            throw e
        }
    }
}