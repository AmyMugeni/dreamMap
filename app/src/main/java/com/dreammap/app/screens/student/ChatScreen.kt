package com.dreammap.app.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    partnerId: String?, // The ID of the person we are chatting with (the Mentor)
    authViewModel: AuthViewModel
) {
    val currentUserName = authViewModel.currentUser.collectAsState().value?.name ?: "Student"

    // ⚠️ TODO: Replace with real ChatViewModel and message list
    val partnerName = if (partnerId == "m1") "Dr. Alex Chen" else "Mentor"

    // Placeholder for message input
    var messageInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Chat with $partnerName",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            ChatInputBar(
                messageInput = messageInput,
                onMessageChange = { messageInput = it },
                onSend = {
                    // 💬 TODO: Implement Firestore write logic here
                    println("Sending message: $it from $currentUserName to $partnerId")
                    messageInput = "" // Clear the input after sending
                }
            )
        }
    ) { paddingValues ->
        // Placeholder for the message list area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            reverseLayout = true, // Start scrolling from the bottom
            verticalArrangement = Arrangement.Bottom
        ) {
            // ⚠️ TODO: Replace with items(messages) { MessageBubble(...) }
            // Sample messages for layout demonstration
            items(10) { index ->
                val isUserMessage = index % 3 == 0
                MessageBubble(
                    text = "This is sample message $index.",
                    isUserMessage = isUserMessage,
                    timestamp = "4:45 PM"
                )
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun ChatInputBar(
    messageInput: String,
    onMessageChange: (String) -> Unit,
    onSend: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = messageInput,
            onValueChange = onMessageChange,
            label = { Text("Message") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { if (messageInput.isNotBlank()) onSend(messageInput) },
            enabled = messageInput.isNotBlank(),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = "Send Message")
        }
    }
}

@Composable
fun MessageBubble(text: String, isUserMessage: Boolean, timestamp: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isUserMessage) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(text = text, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}