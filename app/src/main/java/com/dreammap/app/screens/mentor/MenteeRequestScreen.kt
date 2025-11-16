package com.dreammap.app.screens.mentor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dreammap.app.data.model.MentorshipRequest
import com.dreammap.app.viewmodels.MentorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenteeRequestsScreen(
    navController: NavController,
    mentorViewModel: MentorViewModel
) {
    val requests by mentorViewModel.pendingRequests.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mentorship Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (requests.isEmpty() && !isProcessing) {
            EmptyState(paddingValues)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(requests, key = { it.id ?: "" }) { request ->
                    RequestCard(
                        request = request,
                        onAccept = {
                            isProcessing = true
                            mentorViewModel.acceptRequest(request)
                            scope.launch {
                                snackbarHostState.showSnackbar("Accepted ${request.studentName}")
                                isProcessing = false
                            }
                        },
                        onDecline = {
                            isProcessing = true
                            mentorViewModel.declineRequest(request)
                            scope.launch {
                                snackbarHostState.showSnackbar("Declined ${request.studentName}")
                                isProcessing = false
                            }
                        },
                        isProcessing = isProcessing
                    )
                }
            }
        }
    }
}

@Composable
fun RequestCard(
    request: MentorshipRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    isProcessing: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = request.studentName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = request.targetRoadmap,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Motivation: ${request.motivationMessage}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Accept")
                    Spacer(Modifier.width(4.dp))
                    Text("Accept")
                }
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Decline")
                    Spacer(Modifier.width(4.dp))
                    Text("Decline")
                }
            }
        }
    }
}

@Composable
fun EmptyState(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No Pending Requests",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "All caught up! You'll see new mentees appear here.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}
