package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.User
import com.dreammap.app.util.constants.FirebaseConstants
import com.google.firebase.Timestamp
import kotlinx.coroutines.channels.awaitClose
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    // Inject or initialize FirebaseFirestore instance
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection(FirebaseConstants.USERS_COLLECTION)

    /**
     * Creates a new user profile document immediately after Firebase Auth registration.
     * @param user The User data model object.
     */
    suspend fun createUserDocument(user: User) {
        // Sets the document ID explicitly to the user's UID for easy lookup
        usersCollection.document(user.uid).set(user).await()
    }

    /**
     * Retrieves a user's profile data from Firestore. Essential for fetching the user's role.
     * @param uid The Firebase User ID (UID).
     * @return The User object or null if not found.
     */
    suspend fun getUser(uid: String): User? {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            // .toObject() maps the document fields to the User data class
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            // Log error
            null
        }
    }

    // --- MENTOR SPECIFIC QUERIES ---

    /**
     * Retrieves all mentors (users where role == "mentor") who are marked as available.
     */
    suspend fun getAvailableMentors(): List<User> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo(FirebaseConstants.FIELD_ROLE, FirebaseConstants.ROLE_MENTOR)
                .whereEqualTo(FirebaseConstants.FIELD_IS_AVAILABLE, true)
                .get().await()

            snapshot.toObjects(User::class.java)
        } catch (e: Exception) {
            // Log error
            emptyList()
        }
    }

    // --- PROFILE UPDATE ---

    /**
     * Updates specific fields in a user's profile (e.g., updating interests or mentor bio).
     */
    suspend fun updateProfile(uid: String, updates: Map<String, Any>) {
        usersCollection.document(uid).update(updates).await()
    }
}