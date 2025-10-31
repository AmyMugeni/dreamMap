package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val userRepository: UserRepository = UserRepository(firestore) // Inject the UserRepository
) {

    // 1. Function to create a new user account via email/password
    suspend fun registerUser(email: String, password: String, role: String, name: String): Result<User> {
        return try {
            // AUTH STEP: Create user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Auth user creation failed.")

            // FIRESTORE STEP: Create the corresponding user profile document
            val newUser = User(
                uid = firebaseUser.uid,
                email = email,
                name = name,
                role = role, // Crucial for role-based access
                dateJoined = com.google.firebase.Timestamp.now()
            )

            userRepository.createUserDocument(newUser) // Uses the function from UserRepository

            Result.success(newUser)
        } catch (e: Exception) {
            // Log the specific error (e.g., email already in use, weak password)
            Result.failure(e)
        }
    }

    // 2. Function to sign in an existing user
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // AUTH STEP: Sign in the user
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid ?: throw Exception("Login failed. No active user.")

            // FIRESTORE STEP: Fetch the user's profile to get their role and data
            val userProfile = userRepository.getUser(uid) ?: throw Exception("User profile not found in database.")

            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Function to sign out
    fun signOut() {
        auth.signOut()
    }
}