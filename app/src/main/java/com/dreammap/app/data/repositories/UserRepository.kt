package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.User
import com.dreammap.app.util.constants.FirebaseConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection(FirebaseConstants.USERS_COLLECTION)

    /**
     * Creates a new user profile document immediately after Firebase Auth registration.
     */
    suspend fun createUserDocument(user: User) {
        try {
            println("DEBUG: Creating user document for UID = ${user.uid}")
            usersCollection.document(user.uid).set(user).await()
            println("DEBUG: User document successfully created in Firestore.")
        } catch (e: Exception) {
            println("ERROR: Failed to create user document for UID = ${user.uid}. Error: ${e.message}")
            throw e
        }
    }

    /**
     * Retrieves a user's profile data from Firestore.
     */
    suspend fun getUser(userId: String): User? {
        return try {
            println("DEBUG: Fetching user profile for UID = $userId")
            val snapshot = usersCollection.document(userId).get().await()
            if (!snapshot.exists()) {
                println("WARNING: No user document found for UID = $userId")
                return null
            }
            val user = snapshot.toObject(User::class.java)
            println("DEBUG: User profile retrieved: $user")
            user
        } catch (e: Exception) {
            println("ERROR: Failed to fetch user profile for UID = $userId. Error: ${e.message}")
            null
        }
    }

    /**
     * Retrieves all mentors who are marked as available.
     */
    suspend fun getAvailableMentors(): List<User> {
        return try {
            println("DEBUG: Fetching available mentors...")
            val snapshot = usersCollection
                .whereEqualTo(FirebaseConstants.FIELD_ROLE, FirebaseConstants.ROLE_MENTOR)
                .whereEqualTo(FirebaseConstants.FIELD_IS_AVAILABLE, true)
                .get().await()

            val mentors = snapshot.toObjects(User::class.java)
            println("DEBUG: Found ${mentors.size} available mentors.")
            mentors
        } catch (e: Exception) {
            println("ERROR: Failed to fetch available mentors. Error: ${e.message}")
            emptyList()
        }
    }

    /**
     * Updates specific fields in a user's profile.
     */
    suspend fun updateMentorProfile(uid: String, updates: Map<String, Any>) {
        try {
            println("DEBUG: Attempting to update mentor profile for UID=$uid with $updates")
            usersCollection.document(uid).update(updates).await()
            println("DEBUG: Mentor profile update successful for UID=$uid")
        } catch (e: Exception) {
            println("ERROR: Mentor profile update failed for UID=$uid. Error=${e.message}")
            throw e
        }
    }

}
