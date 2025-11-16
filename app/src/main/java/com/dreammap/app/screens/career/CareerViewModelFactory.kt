package com.dreammap.app.screens.career

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dreammap.app.data.repositories.RoadmapRepository

class CareerViewModelFactory(
    private val roadmapRepository: RoadmapRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CareerViewModel::class.java)) {
            return CareerViewModel(roadmapRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

