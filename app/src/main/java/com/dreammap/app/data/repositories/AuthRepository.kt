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
            println("DEBUG: FirebaseAuth instance = $auth")
            println("DEBUG: Firebase initialized: ${auth.app.name}")
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
    // In AuthRepository.kt

    // 4. Function to check for the currently signed-in Firebase Auth user
    fun getCurrentFirebaseUser(): com.google.firebase.auth.FirebaseUser? {
        return auth.currentUser
    }

    // 5. Function to fetch profile data, used during the initial load
    suspend fun getProfileByUid(uid: String): Result<User> {
        return try {
            val userProfile = userRepository.getUser(uid)
            if (userProfile != null) {
                Result.success(userProfile)
            } else {
                Result.failure(Exception("User profile not found in database."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}