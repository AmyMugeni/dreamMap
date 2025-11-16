package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.User
import com.dreammap.app.data.repositories.UserRepository
import com.dreammap.app.util.constants.FirebaseConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Students state
    private val _students = MutableStateFlow<List<User>>(emptyList())
    val students: StateFlow<List<User>> = _students

    // Mentors state
    private val _mentors = MutableStateFlow<List<User>>(emptyList())
    val mentors: StateFlow<List<User>> = _mentors

    // All users state
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Selected user for detail view
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser

    // Statistics
    private val _stats = MutableStateFlow(AdminStats())
    val stats: StateFlow<AdminStats> = _stats

    data class AdminStats(
        val totalUsers: Int = 0,
        val totalStudents: Int = 0,
        val totalMentors: Int = 0,
        val totalAdmins: Int = 0
    )

    init {
        loadAllData()
    }

    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val allUsersList = userRepository.getAllUsers()
                val studentsList = userRepository.getUsersByRole(FirebaseConstants.ROLE_STUDENT)
                val mentorsList = userRepository.getUsersByRole(FirebaseConstants.ROLE_MENTOR)
                val adminsList = userRepository.getUsersByRole(FirebaseConstants.ROLE_ADMIN)

                _allUsers.value = allUsersList
                _students.value = studentsList
                _mentors.value = mentorsList

                _stats.value = AdminStats(
                    totalUsers = allUsersList.size,
                    totalStudents = studentsList.size,
                    totalMentors = mentorsList.size,
                    totalAdmins = adminsList.size
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _students.value = userRepository.getUsersByRole(FirebaseConstants.ROLE_STUDENT)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load students: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMentors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _mentors.value = userRepository.getUsersByRole(FirebaseConstants.ROLE_MENTOR)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load mentors: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserDetail(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getUser(userId)
                _selectedUser.value = user
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(userId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.updateUserProfile(userId, updates)
                // Reload data after update
                loadAllData()
                // Reload current user detail if it's the same user
                if (_selectedUser.value?.uid == userId) {
                    loadUserDetail(userId)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update user: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = userRepository.deleteUser(userId)
                if (success) {
                    // Reload data after deletion
                    loadAllData()
                    loadStudents()
                    loadMentors()
                    _selectedUser.value = null
                    onSuccess()
                } else {
                    _errorMessage.value = "Failed to delete user"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete user: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changeUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.updateUserProfile(userId, mapOf("role" to newRole))
                // Reload data after role change
                loadAllData()
                loadStudents()
                loadMentors()
                // Reload current user detail if it's the same user
                if (_selectedUser.value?.uid == userId) {
                    loadUserDetail(userId)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to change user role: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleMentorAvailability(userId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.updateUserProfile(userId, mapOf("available" to isAvailable))
                // Reload data after update
                loadMentors()
                // Reload current user detail if it's the same user
                if (_selectedUser.value?.uid == userId) {
                    loadUserDetail(userId)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update mentor availability: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

