package com.dreammap.app.screens.mentor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.User
import com.dreammap.app.data.model.Booking
import com.dreammap.app.data.repositories.UserRepository
import com.dreammap.app.data.repositories.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp

class MentorViewModel(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    // --- State for Mentor Directory ---

    // All available mentors for the directory screen
    private val _availableMentors = MutableStateFlow<List<User>>(emptyList())
    val availableMentors: StateFlow<List<User>> = _availableMentors

    // The mentor currently selected for viewing/booking
    private val _selectedMentor = MutableStateFlow<User?>(null)
    val selectedMentor: StateFlow<User?> = _selectedMentor

    // --- State for Bookings ---

    // The current user's list of scheduled sessions (both student and mentor view)
    private val _userBookings = MutableStateFlow<List<Booking>>(emptyList())
    val userBookings: StateFlow<List<Booking>> = _userBookings

    // --- General UI State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // Fetch the list of mentors when the ViewModel starts
        fetchAvailableMentors()
    }

    // --- Core Functions ---

    /**
     * Fetches all available mentor profiles for the directory screen.
     */
    fun fetchAvailableMentors() = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        try {
            // This relies on the whereEqualTo(role, mentor) query in UserRepository
            _availableMentors.value = userRepository.getAvailableMentors()
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load mentor directory: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Sets the selected mentor profile for the detail/booking screen.
     */
    fun selectMentor(mentor: User) {
        _selectedMentor.value = mentor
    }

    /**
     * Fetches all bookings relevant to the current user (either as student or mentor).
     * @param userId The UID of the currently authenticated user.
     */
    fun fetchUserBookings(userId: String) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        try {
            _userBookings.value = bookingRepository.getUserBookings(userId)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load your sessions: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Creates a new booking document in Firestore.
     */
    fun bookSession(
        studentId: String,
        mentorId: String,
        studentName: String,
        mentorName: String,
        dateTime: Timestamp, // Passed from the UI date/time picker
        topic: String
    ) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        val newBooking = Booking(
            studentId = studentId,
            mentorId = mentorId,
            dateTime = dateTime,
            topic = topic,
            studentName = studentName,
            mentorName = mentorName
        )

        try {
            bookingRepository.createBooking(newBooking)
            // Optionally, refresh the user's booking list after successful creation
            fetchUserBookings(studentId)
        } catch (e: Exception) {
            _errorMessage.value = "Booking failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Updates the status of a specific booking (e.g., confirmation or cancellation).
     */
    fun updateBookingStatus(bookingId: String, newStatus: String, userId: String) = viewModelScope.launch {
        try {
            bookingRepository.updateBookingStatus(bookingId, newStatus)
            // Refresh the list to reflect the change
            fetchUserBookings(userId)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to update status: ${e.message}"
        }
    }
}