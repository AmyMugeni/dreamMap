package com.dreammap.app.screens.mentor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen

// --- Placeholder Data Models for Mentor View ---

data class Mentee(
    val id: String,
    val name: String,
    val currentRoadmap: String,
    val progressPercent: Int
)

val sampleMentees = listOf(
    Mentee("s1", "Jane Doe", "Data Science Roadmap", 65),
    Mentee("s2", "Michael Lee", "Frontend Dev Roadmap", 20),
    Mentee("s3", "Chloe Kim", "Product Management Roadmap", 90),
    Mentee("s4", "David Rodriguez", "Mobile App Dev Roadmap", 40)
)

// --- Composable Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMenteesScreen(
    navController: NavHostController
) {
    // ⚠️ TODO: Replace with real data observed from a MentorViewModel
    val mentees = sampleMentees

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Mentees (${mentees.size})") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Dashboard")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            items(mentees) { mentee ->
                MenteeListItem(
                    mentee = mentee,
                    onClick = {
                        // Navigate to the Mentee Detail Screen
                        navController.navigate(Screen.MentorGraph.MenteeDetail.createRoute(mentee.id))
                    }
                )
            }
        }
    }
}

// --- Mentee List Item Composable ---

@Composable
fun MenteeListItem(mentee: Mentee, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder
            Icon(
                Icons.Filled.Person,
                contentDescription = "Mentee Avatar",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name
                Text(
                    text = mentee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // Roadmap
                Text(
                    text = mentee.currentRoadmap,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Progress Bar
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = mentee.progressPercent / 100f,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Progress Percentage
            Text(
                text = "${mentee.progressPercent}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary
            )

            // Navigation Arrow
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "View Details",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
