package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CareerQuizViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted

    /**
     * Saves quiz results to the user's profile
     */
    fun saveQuizResults(userId: String, interests: List<String>) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        try {
            val updates = mapOf(
                "quizCompleted" to true,
                "interests" to interests
            )
            userRepository.updateUserProfile(userId, updates)
            _quizCompleted.value = true
        } catch (e: Exception) {
            _errorMessage.value = "Failed to save quiz results: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Processes quiz answers and determines career interests
     */
    fun processQuizAnswers(answers: Map<Int, Int>): List<String> {
        // Map question categories to career interests
        val categoryScores = mutableMapOf<String, Int>()

        // Question 1: Problem-solving preference
        when (answers[1]) {
            0 -> {
                categoryScores["Technology"] = (categoryScores["Technology"] ?: 0) + 3
                categoryScores["Engineering"] = (categoryScores["Engineering"] ?: 0) + 2
            }
            1 -> {
                categoryScores["Business"] = (categoryScores["Business"] ?: 0) + 3
                categoryScores["Finance"] = (categoryScores["Finance"] ?: 0) + 2
            }
            2 -> {
                categoryScores["Healthcare"] = (categoryScores["Healthcare"] ?: 0) + 3
                categoryScores["Science"] = (categoryScores["Science"] ?: 0) + 2
            }
            3 -> {
                categoryScores["Arts"] = (categoryScores["Arts"] ?: 0) + 3
                categoryScores["Design"] = (categoryScores["Design"] ?: 0) + 2
            }
        }

        // Question 2: Work environment
        when (answers[2]) {
            0 -> {
                categoryScores["Technology"] = (categoryScores["Technology"] ?: 0) + 2
                categoryScores["Business"] = (categoryScores["Business"] ?: 0) + 1
            }
            1 -> {
                categoryScores["Healthcare"] = (categoryScores["Healthcare"] ?: 0) + 2
                categoryScores["Education"] = (categoryScores["Education"] ?: 0) + 2
            }
            2 -> {
                categoryScores["Arts"] = (categoryScores["Arts"] ?: 0) + 2
                categoryScores["Design"] = (categoryScores["Design"] ?: 0) + 2
            }
            3 -> {
                categoryScores["Business"] = (categoryScores["Business"] ?: 0) + 2
                categoryScores["Finance"] = (categoryScores["Finance"] ?: 0) + 2
            }
        }

        // Question 3: Preferred activities
        when (answers[3]) {
            0 -> {
                categoryScores["Technology"] = (categoryScores["Technology"] ?: 0) + 3
                categoryScores["Engineering"] = (categoryScores["Engineering"] ?: 0) + 2
            }
            1 -> {
                categoryScores["Arts"] = (categoryScores["Arts"] ?: 0) + 3
                categoryScores["Design"] = (categoryScores["Design"] ?: 0) + 2
            }
            2 -> {
                categoryScores["Business"] = (categoryScores["Business"] ?: 0) + 3
                categoryScores["Marketing"] = (categoryScores["Marketing"] ?: 0) + 2
            }
            3 -> {
                categoryScores["Healthcare"] = (categoryScores["Healthcare"] ?: 0) + 3
                categoryScores["Science"] = (categoryScores["Science"] ?: 0) + 2
            }
        }

        // Question 4: Skills/Strengths
        when (answers[4]) {
            0 -> {
                categoryScores["Technology"] = (categoryScores["Technology"] ?: 0) + 3
                categoryScores["Engineering"] = (categoryScores["Engineering"] ?: 0) + 2
            }
            1 -> {
                categoryScores["Business"] = (categoryScores["Business"] ?: 0) + 3
                categoryScores["Finance"] = (categoryScores["Finance"] ?: 0) + 2
            }
            2 -> {
                categoryScores["Arts"] = (categoryScores["Arts"] ?: 0) + 3
                categoryScores["Design"] = (categoryScores["Design"] ?: 0) + 2
            }
            3 -> {
                categoryScores["Healthcare"] = (categoryScores["Healthcare"] ?: 0) + 3
                categoryScores["Education"] = (categoryScores["Education"] ?: 0) + 2
            }
        }

        // Question 5: Career goals
        when (answers[5]) {
            0 -> {
                categoryScores["Technology"] = (categoryScores["Technology"] ?: 0) + 2
                categoryScores["Engineering"] = (categoryScores["Engineering"] ?: 0) + 2
            }
            1 -> {
                categoryScores["Business"] = (categoryScores["Business"] ?: 0) + 2
                categoryScores["Finance"] = (categoryScores["Finance"] ?: 0) + 2
            }
            2 -> {
                categoryScores["Healthcare"] = (categoryScores["Healthcare"] ?: 0) + 2
                categoryScores["Education"] = (categoryScores["Education"] ?: 0) + 2
            }
            3 -> {
                categoryScores["Arts"] = (categoryScores["Arts"] ?: 0) + 2
                categoryScores["Design"] = (categoryScores["Design"] ?: 0) + 2
            }
        }

        // Get top 3-5 interests based on scores
        val topInterests = categoryScores.toList()
            .sortedByDescending { it.second }
            .take(5)
            .map { it.first }
            .distinct()

        // Return at least 3 interests, or all if less than 3
        return if (topInterests.size >= 3) {
            topInterests.take(5)
        } else {
            // Fallback to common interests if not enough matches
            listOf("Technology", "Business", "Healthcare", "Arts", "Education").take(3)
        }
    }
}

