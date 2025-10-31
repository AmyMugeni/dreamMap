package com.dreammap.app.data.repositories

import com.dreammap.app.data.model.Message
import com.dreammap.app.util.constants.FirebaseConstants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val chatsCollection = firestore.collection(FirebaseConstants.CHATS_COLLECTION)

    // Helper function to generate a consistent chat ID between two users
    private fun getChatId(user1Id: String, user2Id: String): String {
        // Sort IDs alphabetically to ensure the ID is always the same regardless of who starts the chat
        return if (user1Id < user2Id) "${user1Id}_${user2Id}" else "${user2Id}_${user1Id}"
    }

    /**
     * Creates a new chat thread document if one doesn't exist.
     * @param user1Id Student UID.
     * @param user2Id Mentor UID.
     */
    suspend fun createChat(user1Id: String, user2Id: String) {
        val chatId = getChatId(user1Id, user2Id)
        val chatRef = chatsCollection.document(chatId)

        // Use a transaction or check if the document exists before setting to avoid overwriting
        val chatExists = chatRef.get().await().exists()

        if (!chatExists) {
            chatRef.set(mapOf(
                "participants" to listOf(user1Id, user2Id),
                "createdAt" to Timestamp.now()
            )).await()
        }
    }

    /**
     * Sends a new message to a specific chat.
     */
    suspend fun sendMessage(senderId: String, recipientId: String, text: String) {
        val chatId = getChatId(senderId, recipientId)
        val message = Message(
            senderId = senderId,
            text = text,
            timestamp = Timestamp.now()
        )

        // Writes the message to the 'messages' sub-collection
        chatsCollection.document(chatId)
            .collection(FirebaseConstants.MESSAGES_SUBCOLLECTION)
            .add(message).await()
    }

    /**
     * Sets up a real-time listener for messages in a chat.
     * This uses Kotlin Flow to stream updates to the UI layer.
     */
    fun getMessagesForChat(user1Id: String, user2Id: String): Flow<List<Message>> = callbackFlow {
        val chatId = getChatId(user1Id, user2Id)

        val registration = chatsCollection.document(chatId)
            .collection(FirebaseConstants.MESSAGES_SUBCOLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING) // Order chronologically
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // Close the flow on error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.toObjects(Message::class.java)
                    trySend(messages) // Emit the new list of messages
                }
            }
        // When the flow is closed, remove the listener
        awaitClose { registration.remove() }
    }
}