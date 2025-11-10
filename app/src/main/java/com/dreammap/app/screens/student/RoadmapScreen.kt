package com.dreammap.app.screens.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen
import com.dreammap.app.screens.auth.AuthViewModel // Still passed in NavGraph
import com.dreammap.app.data.model.Roadmap
import com.dreammap.app.data.model.sampleRoadmaps// Assume this is imported
// Assuming sampleRoadmaps is defined or imported from your data file


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen( // <<<--- FUNCTION NAME CORRECTED
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val roadmaps = sampleRoadmaps // Use sample data for now

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Roadmaps") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Open Filter Dialog */ }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
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
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(roadmaps) { roadmap ->
                    RoadmapListItem(
                        roadmap = roadmap,
                        onClick = {
                            navController.navigate(Screen.HomeGraph.RoadmapDetail.createRoute(roadmap.id))
                        }
                    )
                }
            }
        }
    }
}

// --- IN RoadmapScreen.kt (Replacing the old RoadmapListItem) ---

@Composable
fun RoadmapListItem(roadmap: Roadmap, onClick: () -> Unit) {
    // Placeholder: Assuming 5 mentors for visual example since mentor count isn't in your Roadmap model yet.
    // NOTE: If you added 'mentorCount' to the Roadmap model, use 'roadmap.mentorCount' here.
    val mentorCount = 5

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // --- Title ---
            Text(
                text = roadmap.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            // --- Description ---
            Text(
                text = roadmap.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            // --- Metadata Row (Time, Skills, Mentors) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. ✅ NEW: Weekly Time Commitment
                AssistChip(
                    onClick = { /* Does nothing */ },
                    label = { Text(roadmap.weeklyTimeCommitment.ifEmpty { "Flexible" }) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 2. Skills Required (Showing count)
                AssistChip(
                    onClick = { /* Does nothing */ },
                    label = { Text("${roadmap.skillsRequired.size} Skills") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 3. Mentor Count (Placeholder using Star)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Mentors available",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$mentorCount Mentors",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}