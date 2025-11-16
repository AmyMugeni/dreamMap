package com.dreammap.app.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen
import com.dreammap.app.data.model.User
import com.dreammap.app.screens.auth.AuthViewModel
import com.dreammap.app.viewmodels.MentorViewModel
import com.dreammap.app.viewmodels.MentorshipStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorDetailScreen(
    navController: NavHostController,
    mentorId: String?,
    authViewModel: AuthViewModel,
    mentorViewModel: MentorViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val studentId = currentUser?.uid

    // Fetch mentor details + mentorship status
    LaunchedEffect(mentorId, studentId) {
        if (mentorId != null) {
            mentorViewModel.fetchSelectedMentor(mentorId)
        }
        if (mentorId != null && studentId != null) {
            mentorViewModel.checkMentorshipStatus(studentId, mentorId)
        }
    }

    val selectedMentor by mentorViewModel.selectedMentor.collectAsState()
    val mentorshipStatus by mentorViewModel.mentorshipStatus.collectAsState()
    val isLoading by mentorViewModel.isLoading.collectAsState()

    val showRequestDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedMentor?.name ?: "Mentor Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            selectedMentor?.let { mentor ->

                ActionButton(
                    status = mentorshipStatus,
                    onSendRequest = { showRequestDialog.value = true },
                    onStartChat = {
                        navController.navigate(
                            Screen.HomeGraph.Chat.createRoute(mentor.uid)
                        )
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                selectedMentor == null -> Text(
                    "Mentor not found",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )

                else -> MentorProfileContent(mentor = selectedMentor!!)
            }
        }
    }

    // ---- Mentorship Request Dialog ----
    if (showRequestDialog.value && selectedMentor != null && studentId != null) {
        MentorshipRequestDialog(
            mentorName = selectedMentor!!.name,
            onDismiss = { showRequestDialog.value = false },
            onSend = { motivation, roadmap ->
                mentorViewModel.sendMentorshipRequest(
                    studentId = studentId,
                    studentName = currentUser?.name ?: "Unknown",
                    mentorId = selectedMentor!!.uid,
                    mentorName = selectedMentor!!.name,
                    motivationMessage = motivation,
                    targetRoadmap = roadmap
                )
                showRequestDialog.value = false
            }
        )
    }
}

// ---------------- Dynamic Action Button ----------------
@Composable
fun ActionButton(
    status: MentorshipStatus,
    onSendRequest: () -> Unit,
    onStartChat: () -> Unit
) {
    when (status) {
        MentorshipStatus.ACCEPTED -> {
            ExtendedFloatingActionButton(
                onClick = onStartChat,
                icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Start Chat") },
                text = { Text("Start Chat") },
                containerColor = MaterialTheme.colorScheme.secondary
            )
        }
        MentorshipStatus.PENDING -> {
            Button(
                onClick = { /* Do nothing */ },
                enabled = false,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Request Pending")
                Spacer(Modifier.width(8.dp))
                Text("Request Pending")
            }
        }
        MentorshipStatus.NONE, MentorshipStatus.DECLINED -> {
            ExtendedFloatingActionButton(
                onClick = onSendRequest,
                icon = { Icon(Icons.Filled.Person, contentDescription = "Send Request") },
                text = { Text("Send Mentorship Request") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ---------------- Mentorship Request Dialog ----------------
@Composable
fun MentorshipRequestDialog(
    mentorName: String,
    onDismiss: () -> Unit,
    onSend: (motivation: String, roadmap: String) -> Unit
) {
    var motivationText by remember { mutableStateOf("") }
    var roadmapText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Mentorship from $mentorName") },
        text = {
            Column {
                Text(
                    "Tell $mentorName why you want to work with them and which roadmap you\'ll focus on.",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = roadmapText,
                    onValueChange = { roadmapText = it },
                    label = { Text("Target Roadmap") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = motivationText,
                    onValueChange = { motivationText = it },
                    label = { Text("Your Motivation") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(motivationText, roadmapText) },
                enabled = motivationText.isNotBlank() && roadmapText.isNotBlank()
            ) {
                Text("Send Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ---------------- Mentor Profile Content ----------------

@Composable
fun MentorProfileContent(mentor: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Name and Role
        Text(
            text = mentor.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = mentor.role.replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Expertise
        Text("Expertise:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(
            text = mentor.expertise.joinToString(", ").ifEmpty { "Not specified" },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Roadmaps
        Text("Roadmaps Available for Mentorship:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            mentor.roadmaps?.forEach { roadmap ->
                Chip(label = roadmap)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Availability
        Text("Typical Availability:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(
            text = if (mentor.isAvailable) "Available" else "Not available",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bio
        Text("Bio:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(
            text = mentor.bio ?: "Bio not available",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun Chip(label: String) {
    AssistChip(
        onClick = { },
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
