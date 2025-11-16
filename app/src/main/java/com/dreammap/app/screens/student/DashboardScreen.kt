package com.dreammap.app.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
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
import com.dreammap.app.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val userName = user?.name?.split(" ")?.firstOrNull() ?: "Explorer"

    val PrimaryAccent = MaterialTheme.colorScheme.primary
    val SecondaryAccent = MaterialTheme.colorScheme.tertiary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DreamMap Hub") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.HomeGraph.Profile.route) }) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Use LazyColumn for smooth scrolling, which is good for dashboards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // --- 1. Personalized Welcome Banner ---
                WelcomeBanner(userName = userName)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                // --- 2. Action Area: Key Features ---
                Text(
                    text = "Chart Your Course",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                ActionCard(
                    icon = Icons.Filled.Timeline,
                    iconColor = PrimaryAccent,
                    title = "Explore Roadmaps",
                    description = "Discover structured paths to your career goals.",
                    onClick = {
                        navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.Roadmaps.route}")

                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionCard(
                    icon = Icons.Filled.People,
                    iconColor = SecondaryAccent,
                    title = "Find a Mentor",
                    description = "Connect with experts for personalized guidance.",
                    onClick = {
                        navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.Mentors.route}")

                    }
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                // --- 3. Progress/Status Placeholder ---
                Text(
                    text = "Your Current Status",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Placeholder for progress bar, latest task, or goal tracking info
                        Text("You are currently following 0 roadmap.")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(64.dp)) }
        }
    }
}

// --- Helper Composables ---

@Composable
fun WelcomeBanner(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Hello, $userName!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keep track of your progress and connect with your future.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ActionCard(
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
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ListAlt,
                contentDescription = "Navigate",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}