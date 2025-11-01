package com.dreammap.app.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.User
import com.dreammap.app.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 1. User State: Holds the currently logged-in User object or null.
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // 2. Loading State: Indicates if an authentication operation is in progress.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Error State: Displays any authentication errors to the user.
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // Check for an existing session when the ViewModel is created
        handleInitialAuthCheck()
    }

    // --- Core State Management Functions ---

    private fun handleInitialAuthCheck() {
        // If a Firebase Auth user exists, fetch their profile from Firestore
        val authUser = authRepository.getCurrentFirebaseUser()
        if (authUser != null) {
            fetchUserProfile(authUser.uid)
        }
    }

    private fun fetchUserProfile(uid: String) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        // This uses the function we added to AuthRepository
        val result = authRepository.getProfileByUid(uid)

        result.onSuccess { user ->
            _currentUser.value = user
        }.onFailure { e ->
            _errorMessage.value = "Failed to load user profile: ${e.message}"
            authRepository.signOut()
        }
        _isLoading.value = false
    }

    /**
     * Handles user sign-up, role assignment, and subsequent profile creation.
     */
    fun registerUser(email: String, password: String, name: String, role: String) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        val result = authRepository.registerUser(email, password, role, name)

        result.onSuccess { user ->
            _currentUser.value = user
        }.onFailure { e ->
            _errorMessage.value = e.message ?: "Registration failed."
        }
        _isLoading.value = false
    }

    /**
     * Handles user sign-in and fetches the corresponding Firestore profile.
     */
    fun loginUser(email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        val result = authRepository.signIn(email, password)

        result.onSuccess { user ->
            _currentUser.value = user
        }.onFailure { e ->
            _errorMessage.value = e.message ?: "Login failed. Check credentials."
        }
        _isLoading.value = false
    }

    /**
     * Clears user session and state.
     */
    fun logout() {
        authRepository.signOut()
        _currentUser.value = null
        _errorMessage.value = null
    }
}