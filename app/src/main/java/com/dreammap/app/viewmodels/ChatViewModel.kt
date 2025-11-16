package com.dreammap.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreammap.app.data.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private var currentChatId: String? = null
    private var currentUserId: String? = null

    /**
     * Initializes the chat for a given current user and partner.
     */
    fun initializeChat(currentUserId: String, otherUserId: String) {
        this.currentUserId = currentUserId

        // Create a consistent chat ID so both users share the same chat
        currentChatId = if (currentUserId < otherUserId) {
            "${currentUserId}_$otherUserId"
        } else {
            "${otherUserId}_$currentUserId"
        }

        listenToMessages(currentChatId!!)
    }

    /**
     * Starts listening for real-time messages from Firestore.
     */
    private fun listenToMessages(chatId: String) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val messageList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)
                } ?: emptyList()

                _messages.value = messageList
            }
    }

    /**
     * Sends a new message to the current chat.
     */
    fun sendMessage(text: String) {
        val senderId = currentUserId ?: return
        val chatId = currentChatId ?: return
        if (text.isBlank()) return

        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            text = text.trim(),
            timestamp = Timestamp.now(),
            isRead = false
        )

        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(newMessage.id)
            .set(newMessage)
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun isCurrentUser(senderId: String): Boolean = senderId == currentUserId
}
