package com.dreammap.app.viewmodels

import com.dreammap.app.data.model.Roadmap

// Represents the different states your UI can be in
sealed class RoadmapUiState {
    object Loading : RoadmapUiState()
    data class Success(val roadmap: Roadmap) : RoadmapUiState()
    data class Error(val message: String) : RoadmapUiState()
}
