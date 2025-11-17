package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dreammap.app.data.repositories.RoadmapRepository
import com.dreammap.app.data.repositories.UserRepository

class CareerQuizViewModelFactory(
    private val userRepository: UserRepository,
    private val roadmapRepository: RoadmapRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CareerQuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CareerQuizViewModel(userRepository, roadmapRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

