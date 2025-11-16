package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.User
import com.dreammap.app.data.model.Mentor
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
            println("DEBUG: Starting user registration...")
            println("DEBUG: FirebaseAuth instance = $auth")
            println("DEBUG: Firebase app name = ${auth.app.name}")

            // AUTH STEP: Create user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Auth user creation failed.")
            println("DEBUG: Auth user created with UID = ${firebaseUser.uid}")

            // FIRESTORE STEP: Create the corresponding user profile document
            val newUser = User(
                uid = firebaseUser.uid,
                email = email,
                name = name,
                role = role, // Crucial for role-based access
                dateJoined = com.google.firebase.Timestamp.now()
            )
            println("DEBUG: Creating user document in Firestore for UID = ${firebaseUser.uid}")
            userRepository.createUserDocument(newUser)
            println("DEBUG: User document successfully created.")

            if (role == "mentor") {
                val newMentor = Mentor(
                    id = firebaseUser.uid,
                    name = name,
                    expertise = "Not specified",
                    bioSummary = "Bio not available",
                    rating = 0.0,
                    focusRoadmapIds = emptyList()
                )
                println("DEBUG: Creating mentor document in Firestore for UID = ${firebaseUser.uid}")
                firestore.collection("mentors").document(firebaseUser.uid).set(newMentor).await()
                println("DEBUG: Mentor document successfully created.")
            }

            Result.success(newUser)
        } catch (e: Exception) {
            println("ERROR: User registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    // 2. Function to sign in an existing user
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            println("DEBUG: Signing in user with email: $email")
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid ?: throw Exception("Login failed. No active user.")
            println("DEBUG: User signed in with UID = $uid")

            // FIRESTORE STEP: Fetch the user's profile
            val userProfile = userRepository.getUser(uid)
            println("DEBUG: Retrieved user profile: $userProfile")
            if (userProfile == null) throw Exception("User profile not found in database.")

            Result.success(userProfile)
        } catch (e: Exception) {
            println("ERROR: User sign-in failed: ${e.message}")
            Result.failure(e)
        }
    }

    // 3. Function to sign out
    fun signOut() {
        println("DEBUG: Signing out current user.")
        auth.signOut()
    }

    // 4. Function to check for the currently signed-in Firebase Auth user
    fun getCurrentFirebaseUser(): com.google.firebase.auth.FirebaseUser? {
        val current = auth.currentUser
        println("DEBUG: Current Firebase user = $current")
        return current
    }

    // 5. Function to fetch profile data
    suspend fun getProfileByUid(uid: String): Result<User> {
        return try {
            println("DEBUG: Fetching user profile for UID = $uid")
            val userProfile = userRepository.getUser(uid)
            if (userProfile != null) {
                println("DEBUG: User profile found: $userProfile")
                Result.success(userProfile)
            } else {
                println("WARNING: User profile not found for UID = $uid")
                Result.failure(Exception("User profile not found in database."))
            }
        } catch (e: Exception) {
            println("ERROR: Failed to fetch profile: ${e.message}")
            Result.failure(e)
        }
    }
}
