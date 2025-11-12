package com.dreammap.app.screens.mentor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.data.model.MenteeProfile
import com.dreammap.app.viewmodels.MentorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveMenteesScreen(navController: NavHostController, mentorViewModel: MentorViewModel) {
    // Collect the mentee list and mentor name from the ViewModel
    val mentees by mentorViewModel.activeMentees.collectAsState()
    val selectedMentor by mentorViewModel.selectedMentor.collectAsState()

    val mentorName = selectedMentor?.name ?: "Mentor"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Active Mentees") },
                actions = {
                    IconButton(onClick = { /* TODO: Implement search/filtering */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search Mentees")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Welcome Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Welcome back, $mentorName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "You are currently mentoring ${mentees.size} individuals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            // Mentee List
            AnimatedVisibility(
                visible = mentees.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mentees, key = { it.id }) { mentee ->
                        MenteeListItemCard(
                            mentee = mentee,
                            onClick = {
                                // Navigate to MenteeDetailScreen, passing the ID
                                navController.navigate("menteeDetail/${mentee.id}")
                            }
                        )
                    }
                }
            }

            // Empty State
            AnimatedVisibility(
                visible = mentees.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmptyMenteesState()
            }
        }
    }
}

/**
 * A concise card item for displaying a mentee in the list.
 */
@Composable
fun MenteeListItemCard(
    mentee: MenteeProfile,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon or Placeholder
                Icon(
                    Icons.Filled.Group,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterVertically),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(16.dp))

                // Text Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mentee.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Map,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = mentee.currentRoadmap,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Progress Indicator or Navigation Arrow
                Spacer(Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Circular Progress Indicator for overall progress
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = mentee.progressPercent / 100f,
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "${mentee.progressPercent.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Footer (Goals completed)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.MilitaryTech,
                    contentDescription = "Goals Completed",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${mentee.completedGoals} / ${mentee.totalGoals} Goals Completed",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/**
 * Component displayed when the mentor has no active mentees.
 */
@Composable
fun EmptyMenteesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Group,
            contentDescription = "No Mentees",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Time to make a difference!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "You don't have any active mentees yet. Review pending requests to get started.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
