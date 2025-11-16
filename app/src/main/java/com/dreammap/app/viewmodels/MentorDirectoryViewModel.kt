package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.User
import com.dreammap.app.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MentorDirectoryViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _mentors = MutableStateFlow<List<User>>(emptyList())
    val mentors: StateFlow<List<User>> = _mentors

    fun loadMentors() {
        viewModelScope.launch {
            _mentors.value = userRepository.getAvailableMentors()
        }
    }

    private val _mentorDetail = MutableStateFlow<User?>(null)
    val mentorDetail: StateFlow<User?> = _mentorDetail

    fun loadMentorDetail(id: String) {
        viewModelScope.launch {
            _mentorDetail.value = userRepository.getUser(id)
        }
    }
}
