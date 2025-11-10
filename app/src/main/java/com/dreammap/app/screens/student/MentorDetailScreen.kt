package com.dreammap.app.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen // REQUIRED IMPORT
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow // REQUIRED IMPORT

// ⚠️ Placeholder Data Model (Replace with actual data.model.Mentor when integrated)
data class Mentor(
    val id: String,
    val name: String,
    val expertise: String,
    val bioSummary: String,
    val rating: Double,
    val focusRoadmapIds: List<String>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // Added ExperimentalLayoutApi
@Composable
fun MentorDetailScreen(
    navController: NavHostController,
    mentorId: String?
) {
    // ⚠️ TODO: Replace with actual ViewModel call to fetch mentor by ID
    val mentor = mentorId?.let { getSampleMentorDetail(it) }

    if (mentor == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Mentor not found or loading...")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mentor.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (mentorId != null) {
                        // Navigate to the Chat route
                        navController.navigate(Screen.HomeGraph.Chat.createRoute(mentorId))
                    }
                },
                icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") },
                text = { Text("Start Chat") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
        ) {
            item {
                // --- 1. Header and Rating ---
                MentorHeader(mentor = mentor)

                // --- 2. Full Bio ---
                Text(
                    text = "About Me:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
                Text(
                    text = mentor.bioSummary, // Replace with a full bio field later
                    style = MaterialTheme.typography.bodyLarge
                )

                // --- 3. Expertise Chips ---
                Text(
                    text = "Areas of Expertise:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Placeholder chips based on expertise/skills
                    TextChip(label = mentor.expertise, color = MaterialTheme.colorScheme.primary)
                    TextChip(label = "Career Planning", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

// --- Helper Functions ---

@Composable
fun MentorHeader(mentor: Mentor) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Large Profile Placeholder
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(mentor.name.first().toString(), style = MaterialTheme.typography.headlineLarge)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(mentor.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${mentor.rating} / 5.0", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun TextChip(label: String, color: androidx.compose.ui.graphics.Color) {
    AssistChip(
        onClick = {},
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        )
    )
}

// ⚠️ Sample data function for the detail screen (MUST BE REMOVED LATER)
fun getSampleMentorDetail(id: String): Mentor {
    return Mentor(
        id = id,
        name = "Dr. Alex Chen",
        expertise = "Frontend Dev & UX",
        bioSummary = "Dr. Alex Chen is a seasoned software engineer with a deep passion for clean architecture and human-centered design. He has mentored dozens of students in React and scalable application development. He believes mentorship is key to unlocking potential.",
        rating = 4.8,
        focusRoadmapIds = listOf("1", "4")
    )
}