package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.Milestone
import com.dreammap.app.data.model.Roadmap
import com.dreammap.app.data.repositories.RoadmapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// UI state for a list of roadmaps
sealed class RoadmapListUiState {
    object Loading : RoadmapListUiState()
    data class Success(val roadmaps: List<Roadmap>) : RoadmapListUiState()
    data class Error(val message: String) : RoadmapListUiState()
}

// UI state for a single roadmap detail
sealed class RoadmapDetailUiState {
    object Loading : RoadmapDetailUiState()
    data class Success(val roadmap: Roadmap) : RoadmapDetailUiState()
    data class Error(val message: String) : RoadmapDetailUiState()
}

class RoadmapViewModel(
    private val repository: RoadmapRepository
) : ViewModel() {

    // State for list of all roadmaps
    private val _listUiState = MutableStateFlow<RoadmapListUiState>(RoadmapListUiState.Loading)
    val listUiState: StateFlow<RoadmapListUiState> = _listUiState.asStateFlow()

    // State for a single roadmap detail
    private val _detailUiState = MutableStateFlow<RoadmapDetailUiState>(RoadmapDetailUiState.Loading)
    val detailUiState: StateFlow<RoadmapDetailUiState> = _detailUiState.asStateFlow()

    init {
        // Initialize dummy data on startup
        viewModelScope.launch { repository.initializeRoadmapDataIfNeeded() }
        // Load all roadmaps for the list screen
        loadAllRoadmaps()
    }

    /** Fetches all roadmaps for the list view */
    fun loadAllRoadmaps() {
        viewModelScope.launch {
            _listUiState.value = RoadmapListUiState.Loading
            try {
                val roadmaps = repository.getAllRoadmaps()
                _listUiState.value = if (roadmaps.isNotEmpty()) {
                    RoadmapListUiState.Success(roadmaps)
                } else {
                    RoadmapListUiState.Error("No roadmaps found")
                }
            } catch (e: Exception) {
                _listUiState.value = RoadmapListUiState.Error("Error loading roadmaps: ${e.message}")
            }
        }
    }

    /** Listen for a single roadmap by ID for the detail screen */
    fun loadRoadmapDetail(roadmapId: String) {
        _detailUiState.value = RoadmapDetailUiState.Loading

        repository.getRoadmap(roadmapId)
            .onEach { roadmap ->
                if (roadmap != null && roadmap.title.isNotEmpty()) {
                    _detailUiState.value = RoadmapDetailUiState.Success(roadmap)
                } else {
                    _detailUiState.value = RoadmapDetailUiState.Error("Roadmap not found")
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Toggles milestone completion for the currently loaded roadmap
     */
    fun toggleMilestoneCompletion(milestoneId: String, isCompleted: Boolean) {
        val currentState = _detailUiState.value
        if (currentState is RoadmapDetailUiState.Success) {
            val roadmap = currentState.roadmap
            val updatedMilestones = roadmap.milestones.map { milestone ->
                if (milestone.id == milestoneId) {
                    milestone.copy(isCompleted = isCompleted)
                } else milestone
            }
            _detailUiState.value = RoadmapDetailUiState.Success(roadmap.copy(milestones = updatedMilestones))
        }
    }
}
