package com.dreammap.app.screens.mentor


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.data.model.MenteeProfile
import com.dreammap.app.data.model.MentorshipRequest
import com.dreammap.app.viewmodels.MentorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorDashboardScreen(
    navController: NavHostController,
    mentorViewModel: MentorViewModel
) {
    val pendingRequests by mentorViewModel.pendingRequests.collectAsState(initial = emptyList())
    val activeMentees by mentorViewModel.activeMentees.collectAsState(initial = emptyList())
    val isLoading by mentorViewModel.isLoading.collectAsState()


    // Assuming we have a way to get the mentorId, usually via dependency injection or auth
    // For now, we'll use a placeholder and rely on the ViewModel to fetch data on init
    val mentorId = "current_mentor_id"


    LaunchedEffect(mentorId) {
        // Fetch data when the screen is first composed
        mentorViewModel.fetchPendingRequests(mentorId)
        mentorViewModel.fetchActiveMentees(mentorId)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mentor Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {


            if (isLoading && pendingRequests.isEmpty() && activeMentees.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pending Requests Section
                    item {
                        SectionHeader(title = "Mentorship Requests (${pendingRequests.size})", icon = Icons.Default.HourglassEmpty)
                    }
                    if (pendingRequests.isEmpty()) {
                        item { InfoMessage(text = "No new pending requests.") }
                    } else {
                        items(pendingRequests, key = { it.id ?: it.studentId }) { request ->
                            PendingRequestItem(
                                request = request,
                                onAccept = { mentorViewModel.acceptRequest(request) },
                                onDecline = { mentorViewModel.declineRequest(request) },
                                onClick = { navController.navigate("mentor/requestDetail/${request.id}") }
                            )
                        }
                    }


                    // Active Mentees Section
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader(title = "Active Mentees (${activeMentees.size})", icon = Icons.Default.People)
                    }
                    if (activeMentees.isEmpty()) {
                        item { InfoMessage(text = "You currently have no active mentees.") }
                    } else {
                        items(activeMentees, key = { it.id }) { mentee ->
                            MenteeItem(mentee) {
                                // Navigate to mentee profile
                                navController.navigate("mentor/menteeDetail/${mentee.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun InfoMessage(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
fun PendingRequestItem(
    request: MentorshipRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Request from ${request.studentName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.PendingActions,
                    contentDescription = "Pending",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Roadmap: ${request.targetRoadmap}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Decline")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAccept
                ) {
                    Text("Accept")
                }
            }
        }
    }
}


@Composable
fun MenteeItem(mentee: MenteeProfile, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mentee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active Mentee",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Current Roadmap: ${mentee.currentRoadmap}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))


            // Progress Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Progress:",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = mentee.progressPercent.toFloat() / 100f,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${mentee.progressPercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// Helper Composable for clipping (since .clip is experimental in some contexts)
fun Modifier.clip(shape: RoundedCornerShape): Modifier = this