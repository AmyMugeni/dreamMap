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

enum class MentorshipStatus {
    NONE, PENDING, ACCEPTED, DECLINED
}

class MentorViewModel(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val mentorshipRepository: MentorshipRepository
) : ViewModel() {

    private val _pendingRequests = MutableStateFlow<List<MentorshipRequest>>(emptyList())
    val pendingRequests: StateFlow<List<MentorshipRequest>> = _pendingRequests.asStateFlow()

    private val _activeMentees = MutableStateFlow<List<MenteeProfile>>(emptyList())
    val activeMentees: StateFlow<List<MenteeProfile>> = _activeMentees.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedMentor = MutableStateFlow<User?>(null)
    val selectedMentor: StateFlow<User?> = _selectedMentor.asStateFlow()

    private val _mentorshipStatus = MutableStateFlow(MentorshipStatus.NONE)
    val mentorshipStatus: StateFlow<MentorshipStatus> = _mentorshipStatus.asStateFlow()

    fun fetchSelectedMentor(mentorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedMentor.value = userRepository.getUser(mentorId)
            _isLoading.value = false
        }
    }

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

    fun fetchPendingRequests(mentorId: String) {
        viewModelScope.launch {
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

    fun acceptRequest(request: MentorshipRequest) {
        viewModelScope.launch {
            mentorshipRepository.acceptMentorshipRequest(
                requestId = request.id!!,
                studentId = request.studentId,
                mentorId = request.mentorId
            )
        }
    }

    fun declineRequest(request: MentorshipRequest) {
        viewModelScope.launch {
            mentorshipRepository.declineMentorshipRequest(request.id!!)
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
        }
    }

    fun getMenteeById(menteeId: String): Flow<MenteeProfile?> {
        return activeMentees.map { list -> list.find { it.id == menteeId } }
    }

    fun refreshMentorDashboard(mentorId: String) {
        viewModelScope.launch {
            fetchPendingRequests(mentorId)
            fetchActiveMentees(mentorId)
        }
    }
}
