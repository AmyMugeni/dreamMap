package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.QuizQuestion
import com.dreammap.app.data.model.QuizQuestions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    
    // Quiz state
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex
    
    private val _selectedAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedAnswers: StateFlow<Map<String, String>> = _selectedAnswers
    
    private val _isQuizCompleted = MutableStateFlow(false)
    val isQuizCompleted: StateFlow<Boolean> = _isQuizCompleted
    
    private val _calculatedInterests = MutableStateFlow<List<String>>(emptyList())
    val calculatedInterests: StateFlow<List<String>> = _calculatedInterests
    
    // Questions
    val questions: List<QuizQuestion> = QuizQuestions.questions
    
    val currentQuestion: QuizQuestion?
        get() = if (_currentQuestionIndex.value < questions.size) {
            questions[_currentQuestionIndex.value]
        } else {
            null
        }
    
    val progress: Float
        get() = if (questions.isNotEmpty()) {
            (_currentQuestionIndex.value + 1).toFloat() / questions.size
        } else {
            0f
        }
    
    val isLastQuestion: Boolean
        get() = _currentQuestionIndex.value >= questions.size - 1
    
    /**
     * Select an answer for the current question.
     */
    fun selectAnswer(optionText: String) {
        val currentQ = currentQuestion ?: return
        val updatedAnswers = _selectedAnswers.value.toMutableMap()
        updatedAnswers[currentQ.id] = optionText
        _selectedAnswers.value = updatedAnswers
    }
    
    /**
     * Move to the next question.
     */
    fun nextQuestion() {
        if (!isLastQuestion) {
            _currentQuestionIndex.value = _currentQuestionIndex.value + 1
        }
    }
    
    /**
     * Move to the previous question.
     */
    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value = _currentQuestionIndex.value - 1
        }
    }
    
    /**
     * Calculate interests based on selected answers.
     * Uses a scoring system where each selected option contributes to its associated interests.
     */
    fun calculateInterests(): List<String> {
        val interestScores = mutableMapOf<String, Int>()
        
        // Count how many times each interest appears in selected answers
        _selectedAnswers.value.forEach { (questionId, selectedOptionText) ->
            val question = questions.find { it.id == questionId } ?: return@forEach
            val selectedOption = question.options.find { it.text == selectedOptionText } ?: return@forEach
            
            // Add points for each interest in the selected option
            selectedOption.interests.forEach { interest ->
                interestScores[interest] = (interestScores[interest] ?: 0) + 1
            }
        }
        
        // Get top interests (those with highest scores)
        // If there's a tie, we include all interests with the max score
        val maxScore = interestScores.values.maxOrNull() ?: 0
        val topInterests = interestScores
            .filter { it.value == maxScore }
            .keys
            .toList()
        
        // If we have multiple top interests, return them all
        // Otherwise, return top 3-5 interests by score
        val sortedInterests = interestScores
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .map { it.first }
        
        return if (sortedInterests.size >= 3) {
            sortedInterests
        } else {
            // Fallback: return all unique interests from all answers
            _selectedAnswers.value.values
                .flatMap { selectedOptionText ->
                    questions.flatMap { q ->
                        q.options.find { it.text == selectedOptionText }?.interests ?: emptyList()
                    }
                }
                .distinct()
                .take(5)
        }
    }
    
    /**
     * Complete the quiz and calculate final interests.
     */
    fun completeQuiz() {
        viewModelScope.launch {
            val interests = calculateInterests()
            _calculatedInterests.value = interests
            _isQuizCompleted.value = true
        }
    }
    
    /**
     * Reset the quiz to start over.
     */
    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _isQuizCompleted.value = false
        _calculatedInterests.value = emptyList()
    }
    
    /**
     * Check if current question has been answered.
     */
    fun isCurrentQuestionAnswered(): Boolean {
        val currentQ = currentQuestion ?: return false
        return _selectedAnswers.value.containsKey(currentQ.id)
    }
    
    /**
     * Get selected answer for current question.
     */
    fun getSelectedAnswerForCurrentQuestion(): String? {
        val currentQ = currentQuestion ?: return null
        return _selectedAnswers.value[currentQ.id]
    }
}

