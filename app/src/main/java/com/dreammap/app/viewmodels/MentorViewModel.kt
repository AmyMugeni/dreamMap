package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.MenteeProfile
import com.dreammap.app.data.model.MentorshipRequest
import com.dreammap.app.data.model.User
import com.dreammap.app.data.repositories.BookingRepository
import com.dreammap.app.data.repositories.MentorshipRepository
import com.dreammap.app.data.repositories.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Enum to clearly represent the relationship status
enum class MentorshipStatus {
    NONE, PENDING, ACCEPTED, DECLINED
}

/**
 * ViewModel responsible for all mentor-facing data and business logic.
 * Handles fetching active mentees, pending requests, and processing requests.
 */
class MentorViewModel(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val mentorshipRepository: MentorshipRepository
) : ViewModel() {

    // --- State Flows for Mentor Dashboard ---

    private val _pendingRequests = MutableStateFlow<List<MentorshipRequest>>(emptyList())
    val pendingRequests: StateFlow<List<MentorshipRequest>> = _pendingRequests.asStateFlow()

    private val _activeMentees = MutableStateFlow<List<MenteeProfile>>(emptyList())
    val activeMentees: StateFlow<List<MenteeProfile>> = _activeMentees.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State for a selected mentor's public profile (useful if a mentor views another mentor)
    private val _selectedMentor = MutableStateFlow<User?>(null)
    val selectedMentor: StateFlow<User?> = _selectedMentor.asStateFlow()

    // State for a student viewing their own request status with a mentor
    private val _mentorshipStatus = MutableStateFlow(MentorshipStatus.NONE)
    val mentorshipStatus: StateFlow<MentorshipStatus> = _mentorshipStatus.asStateFlow()

    // --- Initialization and Refresh ---

    fun refreshMentorDashboard(mentorId: String) {
        viewModelScope.launch {
            fetchPendingRequests(mentorId)
            fetchActiveMentees(mentorId)
        }
    }

    // --- Fetching Data ---

    fun fetchSelectedMentor(mentorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mentor = userRepository.getUser(mentorId)
                _selectedMentor.value = mentor
            } catch (e: Exception) {
                println("Could not load mentor: ${e.message}")
                _selectedMentor.value = null
            }
            _isLoading.value = false
        }
    }

    fun fetchPendingRequests(mentorId: String) {
        viewModelScope.launch {
            // Use onEach to update loading state if necessary
            mentorshipRepository.getPendingRequests(mentorId).collect { requests ->
                _pendingRequests.value = requests
            }
        }
    }

    fun fetchActiveMentees(mentorId: String) {
        viewModelScope.launch {
            mentorshipRepository.getActiveMentees(mentorId).collect { mentees ->
                _activeMentees.value = mentees
            }
        }
    }

    /**
     * Finds a specific mentee's profile from the active list.
     * Used by MenteeDetailScreen.
     */
    fun getMenteeById(menteeId: String): Flow<MenteeProfile?> {
        return activeMentees.map { list -> list.find { it.id == menteeId } }
    }

    // --- Student-Side Actions (Viewing Status) ---

    fun checkMentorshipStatus(studentId: String, mentorId: String) {
        viewModelScope.launch {
            mentorshipRepository.getStudentRequestStatus(studentId, mentorId).collect { status ->
                _mentorshipStatus.value = when (status) {
                    "Accepted" -> MentorshipStatus.ACCEPTED
                    "Pending" -> MentorshipStatus.PENDING
                    "Declined" -> MentorshipStatus.DECLINED
                    else -> MentorshipStatus.NONE
                }
            }
        }
    }

    fun sendMentorshipRequest(
        studentId: String,
        studentName: String,
        mentorId: String,
        mentorName: String,
        motivationMessage: String,
        targetRoadmap: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = MentorshipRequest(
                    studentId = studentId,
                    studentName = studentName,
                    mentorId = mentorId,
                    mentorName = mentorName,
                    motivationMessage = motivationMessage,
                    targetRoadmap = targetRoadmap,
                    status = "Pending"
                )
                mentorshipRepository.sendMentorshipRequest(request)
            } catch (e: Exception) {
                println("Error sending request: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    // --- Mentor-Side Actions (Processing Requests) ---

    fun acceptRequest(request: MentorshipRequest) {
        viewModelScope.launch {
            try {
                request.id?.let { requestId ->
                    mentorshipRepository.acceptMentorshipRequest(
                        requestId = requestId,
                        studentId = request.studentId,
                        mentorId = request.mentorId
                    )
                }
                // The underlying flow from getPendingRequests will handle the removal from _pendingRequests
                // and the addition to _activeMentees automatically (assuming repository handles this).
            } catch (e: Exception) {
                println("Error accepting request: ${e.message}")
            }
        }
    }

    fun declineRequest(request: MentorshipRequest) {
        viewModelScope.launch {
            try {
                request.id?.let {
                    mentorshipRepository.declineMentorshipRequest(it)
                }
                // The underlying flow will handle the removal from _pendingRequests.
            } catch (e: Exception) {
                println("Error declining request: ${e.message}")
            }
        }
    }
}