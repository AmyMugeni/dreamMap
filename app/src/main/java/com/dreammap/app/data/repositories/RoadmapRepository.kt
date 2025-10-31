package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.Roadmap
import com.dreammap.app.util.constants.FirebaseConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoadmapRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val roadmapsCollection = firestore.collection(FirebaseConstants.ROADMAPS_COLLECTION)

    // 1. Function to fetch all Roadmaps for the List Screen
    suspend fun getAllRoadmaps(): List<Roadmap> {
        return try {
            val snapshot = roadmapsCollection.get().await()
            snapshot.toObjects(Roadmap::class.java)
        } catch (e: Exception) {
            // Log the error
            emptyList()
        }
    }

    // 2. Function to fetch a specific Roadmap for the Detail Screen
    suspend fun getRoadmapById(id: String): Roadmap? {
        return try {
            val snapshot = roadmapsCollection.document(id).get().await()
            snapshot.toObject(Roadmap::class.java)
        } catch (e: Exception) {
            // Log the error
            null
        }
    }

    // 3. Function to query roadmaps based on quiz results (interests)
    suspend fun getRoadmapsByInterest(interests: List<String>): List<Roadmap> {
        return try {
            val snapshot = roadmapsCollection
                // Queries where the 'recommendedInterests' array contains ANY of the user's interests
                .whereArrayContainsAny("recommendedInterests", interests)
                .get().await()
            snapshot.toObjects(Roadmap::class.java)
        } catch (e: Exception) {
            // Log the error
            emptyList()
        }
    }
}