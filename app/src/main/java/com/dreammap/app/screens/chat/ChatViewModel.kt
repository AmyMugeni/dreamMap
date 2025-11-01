package com.dreammap.app.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.Message
import com.dreammap.app.data.repositories.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    // 1. State for Messages: Holds the real-time list of messages for the current chat.
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // 2. State for Chat Partner: The UID of the person the current user is chatting with.
    private val _partnerId = MutableStateFlow<String?>(null)

    // 3. General UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // This property holds the collector job for the real-time messages.
    private var chatListenerJob: kotlinx.coroutines.Job? = null

    /**
     * Initializes the chat session and starts the real-time listener.
     * @param currentUserId The UID of the logged-in user.
     * @param partnerId The UID of the mentor/student partner.
     */
    fun startChatSession(currentUserId: String, partnerId: String) {
        // Stop any existing listener
        chatListenerJob?.cancel()
        _partnerId.value = partnerId
        _messages.value = emptyList() // Clear old messages

        // 1. Ensure the chat thread document exists
        viewModelScope.launch {
            try {
                // Ensure chat document is created before listening
                chatRepository.createChat(currentUserId, partnerId)

                // 2. Start the real-time listener using Flow
                chatListenerJob = chatRepository.getMessagesForChat(currentUserId, partnerId)
                    .onEach { messageList ->
                        _messages.value = messageList // Update UI state with new messages
                        _isLoading.value = false
                    }
                    .launchIn(viewModelScope) // Launch the collector within the ViewModel's scope
            } catch (e: Exception) {
                _errorMessage.value = "Failed to start chat session: ${e.message}"
            }
        }
    }

    /**
     * Sends a new message to the current chat session.
     * @param senderId The current user's UID.
     * @param text The content of the message.
     */
    fun sendMessage(senderId: String, text: String) {
        val recipientId = _partnerId.value
        if (recipientId == null || text.isBlank()) {
            _errorMessage.value = "Cannot send message. Partner ID is missing or text is blank."
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                chatRepository.sendMessage(senderId, recipientId, text.trim())
            } catch (e: Exception) {
                _errorMessage.value = "Failed to send message: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Ensure the real-time listener is closed when the ViewModel is destroyed
        chatListenerJob?.cancel()
    }
}