package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.Mentor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MentorRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val mentorCollection = db.collection("mentors")

    suspend fun getAllMentors(): List<Mentor> {
        return try {
            val snapshot = mentorCollection.get().await()
            snapshot.toObjects(Mentor::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMentorById(id: String): Mentor? {
        return try {
            val snapshot = mentorCollection.document(id).get().await()
            snapshot.toObject(Mentor::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addMentor(mentor: Mentor): Boolean {
        return try {
            mentorCollection.document(mentor.id).set(mentor).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
