package com.dreammap.app.screens.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen
import com.dreammap.app.data.model.Mentor
import com.dreammap.app.data.model.sampleMentors
import com.dreammap.app.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel // Kept for consistency
) {
    // In a real app, you would observe a MentorViewModel state here
    val mentors = sampleMentors

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mentor Directory") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement Search/Filter */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
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
            items(mentors) { mentor ->
                MentorListItem(
                    mentor = mentor,
                    onClick = {
                        // Navigate to the detail screen
                        navController.navigate(Screen.HomeGraph.MentorDetail.createRoute(mentor.id))
                    }
                )
            }
        }
    }
}

// --- Mentor List Item Composable ---

@Composable
fun MentorListItem(mentor: Mentor, onClick: () -> Unit) {
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
            // Placeholder for Profile Image (Replace with Coil/Glide later)
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(mentor.name.first().toString(), style = MaterialTheme.typography.headlineSmall)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mentor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mentor.expertise,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                // Manually truncate the text to avoid the TextOverflow issue
                val summaryLimit = 35
                val summary = if (mentor.bioSummary.length > summaryLimit) {
                    mentor.bioSummary.take(summaryLimit) + "..."
                } else {
                    mentor.bioSummary
                }
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            // Rating
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = mentor.rating.toString(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
