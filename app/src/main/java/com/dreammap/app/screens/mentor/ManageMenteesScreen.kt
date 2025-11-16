package com.dreammap.app.screens.mentor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dreammap.app.data.model.MentorshipRequest
import com.dreammap.app.viewmodels.MentorViewModel
import kotlinx.coroutines.launch

// Placeholder for the actual data model import, assuming it's in the correct path
// import com.dreammap.app.data.model.MentorshipRequest
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorRequestScreen(navController: NavHostController, mentorViewModel: MentorViewModel) {
    // Collect the real-time flow of pending requests
    val requests by mentorViewModel.pendingRequests.collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Pending Mentorship Requests",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Use AnimatedVisibility for a smooth transition when requests are loaded or emptied
        AnimatedVisibility(
            visible = requests.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp), // Extra space for FABs/nav bars
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(requests, key = { it.id ?: it.studentId }) { request ->
                    MentorshipRequestCard(
                        request = request,
                        onAccept = {
                            mentorViewModel.acceptRequest(it)
                            scope.launch {
                                snackbarHostState.showSnackbar("Accepted ${it.studentName}.")
                            }
                        },
                        onDecline = {
                            mentorViewModel.declineRequest(it)
                            scope.launch {
                                snackbarHostState.showSnackbar("Declined request from ${it.studentName}.")
                            }
                        }
                    )
                }
            }
        }

        // Show the Empty State if no requests are found
        AnimatedVisibility(
            visible = requests.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptyRequestsState(Modifier.padding(paddingValues))
        }
    }
}

/**
 * A highly visible card component for handling a single mentorship request.
 */
@Composable
fun MentorshipRequestCard(
    request: MentorshipRequest,
    onAccept: (MentorshipRequest) -> Unit,
    onDecline: (MentorshipRequest) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        // Use the default Material3 Card colors for a clean look
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Student Name and Title
            Text(
                text = "New Request from ${request.studentName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Target Roadmap/Interest
            Text(
                text = "Target Roadmap: ${request.targetRoadmap}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Student Message/Reason
            Text(
                text = "Message: \"${request.motivationMessage.take(80)}\\u2026\"", // Truncate long messages
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Decline Button (Outlined for less visual weight)
                OutlinedButton(
                    onClick = { onDecline(request) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Decline")
                    Spacer(Modifier.width(4.dp))
                    Text("Decline")
                }

                // Accept Button (Filled Tonal for emphasis)
                Button(
                    onClick = { onAccept(request) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Icon(Icons.Filled.AssignmentTurnedIn, contentDescription = "Accept")
                    Spacer(Modifier.width(4.dp))
                    Text("Accept")
                }
            }
        }
    }
}

/**
 * Component displayed when there are no pending requests.
 */
@Composable
fun EmptyRequestsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.HourglassEmpty,
            contentDescription = "No Pending Requests",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "All caught up!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "You don't have any pending mentorship requests right now.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}