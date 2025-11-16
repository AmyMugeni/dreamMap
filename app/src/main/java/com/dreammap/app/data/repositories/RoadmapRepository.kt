package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.Roadmap
import com.dreammap.app.data.model.StaticRoadmapData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val ROADMAP_COLLECTION = "roadmaps"

class RoadmapRepository(private val firestore: FirebaseFirestore) {

    /**
     * Initializes dummy roadmap data if the collection is empty.
     */
    suspend fun initializeRoadmapDataIfNeeded() {
        val collectionRef = firestore.collection(ROADMAP_COLLECTION)

        try {
            println("--- Checking Roadmap Collection Status ---")
            val snapshot = collectionRef.limit(1).get().await()
            println("Snapshot received. Is Empty: ${snapshot.isEmpty}, Size: ${snapshot.size()}")

            if (snapshot.isEmpty) {
                val roadmaps = StaticRoadmapData.allRoadmaps
                for (roadmap in roadmaps) {
                    collectionRef.document(roadmap.id).set(roadmap).await()
                    println("Inserted roadmap: ${roadmap.title}")
                }
                println("All dummy roadmaps initialized. Total: ${roadmaps.size}")
            } else {
                println("Roadmap collection already has data. Skipping initialization.")
                for (document in snapshot.documents) {
                    println("Existing document ID: ${document.id}")
                }
            }

            println("--- Roadmap Collection Status Check Complete ---")
        } catch (e: Exception) {
            System.err.println("Error initializing roadmap data: ${e.message}")
        }
    }

    /**
     * Returns a Flow that emits updates for a single roadmap by ID.
     */
    fun getRoadmap(roadmapId: String): Flow<Roadmap?> = callbackFlow {
        val listener = firestore.collection(ROADMAP_COLLECTION)
            .document(roadmapId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    System.err.println("[RoadmapRepository] Listener error for $roadmapId: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val roadmap = snapshot.toObject(Roadmap::class.java)
                    trySend(roadmap)
                } else {
                    trySend(null)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Fetch all roadmaps once (suspend function).
     */
    suspend fun getAllRoadmaps(): List<Roadmap> {
        return try {
            firestore.collection(ROADMAP_COLLECTION)
                .get()
                .await()
                .toObjects(Roadmap::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Fetch roadmaps filtered by interests (suspend function).
     */
    suspend fun getRoadmapsByInterest(interests: List<String>): List<Roadmap> {
        if (interests.isEmpty()) return emptyList()

        return try {
            firestore.collection(ROADMAP_COLLECTION)
                .whereArrayContainsAny("recommended_interests", interests)
                .get()
                .await()
                .toObjects(Roadmap::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Fetch a single roadmap by ID (suspend function).
     */
    suspend fun getRoadmapById(roadmapId: String): Roadmap? {
        return try {
            firestore.collection(ROADMAP_COLLECTION)
                .document(roadmapId)
                .get()
                .await()
                .toObject(Roadmap::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Delete a roadmap by ID
     */
    suspend fun deleteRoadmapById(roadmapId: String): Boolean {
        return try {
            firestore.collection(ROADMAP_COLLECTION)
                .document(roadmapId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
