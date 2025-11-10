package com.dreammap.app.screens.mentor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.screens.auth.AuthViewModel
import com.dreammap.app.Screen // Assuming you'll define MentorGraph in Screen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorDashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val userName = user?.name?.split(" ")?.firstOrNull() ?: "Mentor"

    // ⚠️ TODO: Replace with real data fetched from a MentorViewModel
    val menteeCount = 7
    val newRequests = 2
    val activeRoadmaps = 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mentor Hub") },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to Mentor Profile */ }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // --- 1. Personalized Welcome Banner ---
                MentorWelcomeBanner(userName = userName)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                // --- 2. Key Statistics Overview ---
                StatsRow(menteeCount, newRequests, activeRoadmaps)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                // --- 3. Primary Action Area ---
                Text(
                    text = "Admin Actions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                // 🔑 UPDATED: Navigation to Manage Mentees Screen
                MentorActionCard(
                    icon = Icons.Filled.Group,
                    iconColor = MaterialTheme.colorScheme.primary,
                    title = "Manage Mentees",
                    description = "View progress, review check-ins, and send reminders.",
                    onClick = {
                        navController.navigate(Screen.MentorGraph.ManageMentees.route)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                MentorActionCard(
                    icon = Icons.Filled.NotificationsActive,
                    iconColor = MaterialTheme.colorScheme.error,
                    title = "Review $newRequests New Requests",
                    description = "Approve or decline students requesting your mentorship.",
                    onClick = { /* TODO: Nav to Request Queue */ }
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// --- Helper Composables (No changes below this line) ---

@Composable
fun MentorWelcomeBanner(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Welcome back, $userName!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your impact is key to student success.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun StatsRow(menteeCount: Int, newRequests: Int, activeRoadmaps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatBox(Icons.Filled.School, MaterialTheme.colorScheme.primary, "Mentees", menteeCount.toString())
        StatBox(Icons.Filled.Mail, MaterialTheme.colorScheme.error, "New Req", newRequests.toString())
        StatBox(Icons.Filled.Timeline, MaterialTheme.colorScheme.secondary, "Roadmaps", activeRoadmaps.toString())
    }
}

@Composable
fun StatBox(icon: ImageVector, color: Color, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun MentorActionCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(36.dp).padding(end = 12.dp)
            )
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(text = description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}