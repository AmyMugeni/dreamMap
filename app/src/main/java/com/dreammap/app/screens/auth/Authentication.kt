package com.dreammap.app.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.User
import com.dreammap.app.data.repositories.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 1. User State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // 2. Loading State
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Error State
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 4. One-time event for successful registration
    private val _registrationSuccess = Channel<Unit>(Channel.BUFFERED)
    val registrationSuccess: SharedFlow<Unit> = _registrationSuccess.receiveAsFlow()
        .shareIn(viewModelScope, started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed())

    // 5. One-time event for successful login
    private val _loginSuccess = Channel<Unit>(Channel.BUFFERED)
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.receiveAsFlow()
        .shareIn(viewModelScope, started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed())


    init {
        // Start the initial authentication check immediately
        handleInitialAuthCheck()
    }

    // --- Core State Management Functions ---

    private fun handleInitialAuthCheck() = viewModelScope.launch {
        // Ensure loading is set at the start
        _isLoading.value = true
        _errorMessage.value = null

        val authUser = authRepository.getCurrentFirebaseUser()

        if (authUser != null) {
            // If a Firebase Auth user exists, fetch their profile from Firestore
            val result = authRepository.getProfileByUid(authUser.uid)

            result.onSuccess { user ->
                _currentUser.value = user
            }.onFailure { e ->
                _errorMessage.value = "Failed to load user profile: ${e.message}"
                // Sign out if profile loading fails, forcing user to re-authenticate
                authRepository.signOut()
            }
        }

        // CRITICAL FIX: Set loading to FALSE once the entire asynchronous check is complete.
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
            _registrationSuccess.send(Unit) // EMIT EVENT
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
            _loginSuccess.send(Unit) // EMIT EVENT
        }.onFailure { e ->
            _errorMessage.value = e.message ?: "Login failed. Check credentials."
        }
        _isLoading.value = false
    }

    /**
     * Clears error message after it has been displayed.
     */
    fun clearError() {
        _errorMessage.value = null
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