package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dreammap.app.data.repositories.BookingRepository
import com.dreammap.app.data.repositories.MentorshipRepository
import com.dreammap.app.data.repositories.UserRepository

class MentorViewModelFactory(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val mentorshipRepository: MentorshipRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MentorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MentorViewModel(
                userRepository = userRepository,
                bookingRepository = bookingRepository,
                mentorshipRepository = mentorshipRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
