package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.dreammap.app.util.constants.FirebaseConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MentorProfileViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _mentor = MutableStateFlow<User?>(null)
    val mentor: StateFlow<User?> = _mentor

    private val mentorsCollection = firestore.collection(FirebaseConstants.USERS_COLLECTION)

    // --- Fetch mentor profile by UID ---
    fun fetchMentorProfile(uid: String) {
        viewModelScope.launch {
            try {
                val doc = mentorsCollection.document(uid).get().await()
                if (doc.exists()) {
                    _mentor.value = doc.toObject(User::class.java)?.copy(uid = uid)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Save mentor profile updates ---
    fun saveMentorProfile(updatedMentor: User, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                mentorsCollection.document(updatedMentor.uid)
                    .set(updatedMentor)
                    .await()
                _mentor.value = updatedMentor
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
