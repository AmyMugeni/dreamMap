package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dreammap.app.data.repositories.UserRepository

class MentorDirectoryViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MentorDirectoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MentorDirectoryViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
