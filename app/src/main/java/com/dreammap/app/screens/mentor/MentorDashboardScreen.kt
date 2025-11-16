package com.dreammap.app.screens.mentor

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.data.model.MenteeProfile
import com.dreammap.app.data.model.MentorshipRequest
import com.dreammap.app.Screen
import com.dreammap.app.viewmodels.MentorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorDashboardScreen(
    navController: NavHostController,
    mentorViewModel: MentorViewModel,
    mentorId: String, // Receive mentorId from navigation
    mentorName: String // Receive mentorName from navigation
) {
    // Initialize the ViewModel with the mentor's ID
    LaunchedEffect(mentorId) {
        mentorViewModel.refreshMentorDashboard(mentorId)
    }

    // Collect StateFlows for real-time updates
    val pendingRequests by mentorViewModel.pendingRequests.collectAsState()
    val activeMentees by mentorViewModel.activeMentees.collectAsState()
    val isLoading by mentorViewModel.isLoading.collectAsState()

    // Navigation action to the profile edit screen
    val navigateToProfileEdit = {
        navController.navigate(
            "mentor_graph/$mentorId/$mentorName/profile/mentorEdit/$mentorId"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mentor Hub") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                actions = {
                    IconButton(onClick = navigateToProfileEdit) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = isLoading,
                label = "Loading Content Transition"
            ) { isContentLoading ->
                if (isContentLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // WELCOME MESSAGE
                        item {
                            WelcomeCard(mentorName = mentorName)
                        }

                        // Complete Profile Button
                        item {
                            Button(
                                onClick = navigateToProfileEdit,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Complete Your Profile")
                            }
                        }

                        // Pending Requests Section
                        item {
                            SectionHeader(
                                title = "Pending Requests",
                                count = pendingRequests.size,
                                icon = Icons.Default.HourglassBottom,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        if (pendingRequests.isEmpty()) {
                            item { InfoMessage(text = "No new mentorship requests at this time.", icon = Icons.Default.Info) }
                        } else {
                            items(pendingRequests, key = { it.id }) { request ->
                                PendingRequestItem(
                                    request = request,
                                    onAccept = { mentorViewModel.acceptRequest(request) },
                                    onDecline = { mentorViewModel.declineRequest(request) },
                                    onClick = { 
										// Placeholder for request detail navigation
									}
                                )
                            }
                        }

                        // Active Mentees Section
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            SectionHeader(
                                title = "Active Mentees",
                                count = activeMentees.size,
                                icon = Icons.Default.People,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        if (activeMentees.isEmpty()) {
                            item { InfoMessage(text = "You currently have no active mentees.", icon = Icons.Default.SentimentVerySatisfied) }
                        } else {
                            items(activeMentees, key = { it.id }) { mentee ->
                                MenteeItem(
                                    mentee = mentee,
                                    onViewProfile = { navController.navigate(Screen.MentorGraph.MenteeDetail.createRoute(mentee.id)) },
                                    onChat = { navController.navigate(Screen.HomeGraph.Chat.createRoute(mentee.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeCard(mentorName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = mentorName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ready to guide the next generation of talent?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, count: Int, icon: ImageVector, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$count",
                color = color,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
fun InfoMessage(text: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun PendingRequestItem(
    request: MentorshipRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Student Name and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = request.studentName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Pending",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Details
            Text(
                text = "Target Roadmap: ${request.targetRoadmap}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = request.motivationMessage,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onDecline) {
                    Text("Decline")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onAccept) {
                    Text("Accept")
                }
            }
        }
    }
}

@Composable
fun MenteeItem(
    mentee: MenteeProfile,
    onViewProfile: () -> Unit,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mentee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // Add more mentee details here if needed
            }
            Row {
                TextButton(onClick = onViewProfile) {
                    Text("View Profile")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onChat) {
                    Text("Chat")
                }
            }
        }
    }
}