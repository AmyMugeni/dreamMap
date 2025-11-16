package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Using a hardcoded path for demonstration. In a real app, this would be dynamic based on the chat ID.
private const val CHAT_PATH = "chats/chat_mentor_mentee_001/messages"

class ChatRepository(private val firestore: FirebaseFirestore) {

    /**
     * Provides a real-time stream of messages from Firestore using callbackFlow.
     * Messages are ordered by timestamp ascending.
     */
    fun getMessages(): Flow<List<Message>> = callbackFlow {
        // Query the messages collection, ordered by timestamp
        val messagesQuery = firestore.collection(CHAT_PATH)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        // Set up the real-time listener (onSnapshot)
        val subscription = messagesQuery.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle the error (e.g., permission denied, connection loss)
                trySend(emptyList())
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Map Firestore documents to Message data class
                val messages = snapshot.documents.mapNotNull { document ->
                    try {
                        // toObject automatically handles the Timestamp conversion
                        document.toObject(Message::class.java)?.copy(id = document.id)
                    } catch (e: Exception) {
                        // Log error if message mapping fails
                        null
                    }
                }
                // Send the new list of messages to the Flow
                trySend(messages)
            }
        }

        // Suspend until the coroutine is cancelled, then remove the listener
        awaitClose {
            subscription.remove()
        }
    }

    /**
     * Sends a new message to Firestore.
     */
    suspend fun sendMessage(
        senderId: String,
        text: String
    ) {
        val newMessage = Message(
            senderId = senderId,
            text = text,
            timestamp = Timestamp.now()
        )
        // Add the new document to the collection. Firestore will auto-generate the document ID.
        firestore.collection(CHAT_PATH).add(newMessage).await()
    }

    /**
     * Initializes the chat with a couple of messages if the collection is currently empty.
     */
    suspend fun initializeChatDataIfNeeded() {
        val collection = firestore.collection(CHAT_PATH)
        // Check if the collection has any documents
        val initialDocs = collection.limit(1).get().await()

        if (initialDocs.isEmpty) {
            val initialMessages = listOf(
                Message(senderId = "mentee_001", text = "Hi Mentor! I'm ready for the next session.", timestamp = Timestamp(System.currentTimeMillis() / 1000 - 3600, 0)),
                Message(senderId = "mentor_123", text = "Perfect! Let's review your 'State' homework.", timestamp = Timestamp(System.currentTimeMillis() / 1000 - 1800, 0))
            )
            initialMessages.forEach { msg ->
                collection.add(msg).await()
            }
        }
    }
}