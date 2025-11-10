package com.dreammap.app.screens.mentor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// --- Detailed Data Model ---

data class MenteeDetail(
    val id: String,
    val name: String,
    val overallProgress: Int,
    val joinedDate: String,
    val activeRoadmaps: List<MenteeRoadmap>,
    val recentActivity: List<MenteeActivity>
)

data class MenteeRoadmap(
    val id: String,
    val title: String,
    val progress: Int
)

data class MenteeActivity(
    val type: String, // e.g., "Check-in", "Task Completed", "Note"
    val description: String,
    val date: String,
    val icon: ImageVector
)

// --- Sample Data ---

private fun getSampleMenteeDetail(menteeId: String): MenteeDetail? {
    // This simulates fetching data based on the menteeId passed via navigation
    return if (menteeId == "s1") MenteeDetail(
        id = "s1",
        name = "Jane Doe",
        overallProgress = 65,
        joinedDate = "05/10/2024",
        activeRoadmaps = listOf(
            MenteeRoadmap("r1", "Data Science Core", 65),
            MenteeRoadmap("r2", "Advanced Python", 20)
        ),
        recentActivity = listOf(
            MenteeActivity("Note", "Suggested focusing on SQL optimization.", "Today", Icons.Filled.NoteAdd),
            MenteeActivity("Task", "Completed Module 3 Quiz.", "Yesterday", Icons.Filled.CheckCircle),
            MenteeActivity("Check-in", "Scheduled meeting for next week.", "2 days ago", Icons.Filled.Timelapse)
        )
    ) else null
}


// --- Composable Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenteeDetailScreen(
    navController: NavHostController,
    menteeId: String?
) {
    val mentee = menteeId?.let { getSampleMenteeDetail(it) }

    if (mentee == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Mentee not found or loading...")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress for ${mentee.name}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Mentees")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Initiate Chat */ }) {
                        Icon(Icons.Filled.NoteAdd, contentDescription = "Add Note")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                // 1. Overall Status Card
                OverallProgressCard(mentee)
            }

            item {
                // 2. Active Roadmaps Header
                Text(
                    "Active Roadmaps",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            items(mentee.activeRoadmaps) { roadmap ->
                RoadmapProgressItem(roadmap)
            }

            item {
                // 3. Recent Activity/Notes Header
                Text(
                    "Recent Activity & Notes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            items(mentee.recentActivity) { activity ->
                ActivityLogItem(activity)
            }

            item { Spacer(Modifier.height(50.dp)) } // Bottom buffer
        }
    }
}

// --- Helper Composables ---

@Composable
fun OverallProgressCard(mentee: MenteeDetail) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Overall Mentee Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular Progress Indicator
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = mentee.overallProgress / 100f,
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        "${mentee.overallProgress}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        "Engaged & Active",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "Joined: ${mentee.joinedDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RoadmapProgressItem(roadmap: MenteeRoadmap) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(roadmap.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = roadmap.progress / 100f,
                modifier = Modifier.fillMaxWidth(0.95f),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            "${roadmap.progress}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ActivityLogItem(activity: MenteeActivity) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            activity.icon,
            contentDescription = activity.type,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                activity.type,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                activity.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            activity.date,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
