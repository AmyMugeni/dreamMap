package com.dreammap.app.screens.student

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.screens.auth.AuthViewModel
import com.dreammap.app.Screen
import com.dreammap.app.ui.theme.*
import com.dreammap.app.components.DreamMapBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val userName = if (user?.name.isNullOrBlank()) {
        user?.email?.split("@")?.firstOrNull() ?: "User"
    } else {
        user?.name?.split(" ")?.firstOrNull() ?: "User"
    }

    val PrimaryAccent = MaterialTheme.colorScheme.primary
    val SecondaryAccent = MaterialTheme.colorScheme.tertiary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "DreamMap Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            DreamMapBottomNavigation(navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
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
                // --- 3. Gamification: XP & Level Display ---
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                // XP and Level Card
                GamificationCard(
                    currentXP = 250,
                    xpToNextLevel = 500,
                    level = 3,
                    roadmapsCompleted = 0,
                    milestonesCompleted = 0
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                // --- 4. Achievements/Badges Section ---
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                AchievementBadgesRow()
            }

            item { Spacer(modifier = Modifier.height(64.dp)) }
        }
    }
}

// --- Gamification Components ---

@Composable
fun GamificationCard(
    currentXP: Int,
    xpToNextLevel: Int,
    level: Int,
    roadmapsCompleted: Int,
    milestonesCompleted: Int
) {
    val progress = (currentXP.toFloat() / xpToNextLevel).coerceIn(0f, 1f)
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Level Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Gold, LightGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lv.$level",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkPurple
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Level $level Explorer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$currentXP / $xpToNextLevel XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                // XP Icon
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "XP",
                    tint = Gold.copy(alpha = pulseAlpha),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress to Level ${level + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MediumPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = MediumPurple,
                    trackColor = LightPurple.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Filled.Timeline,
                    value = roadmapsCompleted.toString(),
                    label = "Roadmaps",
                    color = MediumPurple
                )
                StatItem(
                    icon = Icons.Filled.CheckCircle,
                    value = milestonesCompleted.toString(),
                    label = "Milestones",
                    color = LightPurple
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun AchievementBadgesRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Example badges - you can expand this with actual achievement data
        AchievementBadge(
            icon = Icons.Filled.Star,
            label = "First Steps",
            unlocked = true,
            color = Gold,
            modifier = Modifier.weight(1f)
        )
        AchievementBadge(
            icon = Icons.Filled.Timeline,
            label = "Roadmap Master",
            unlocked = false,
            color = MediumPurple,
            modifier = Modifier.weight(1f)
        )
        AchievementBadge(
            icon = Icons.Filled.People,
            label = "Mentor Connect",
            unlocked = false,
            color = Pink,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AchievementBadge(
    icon: ImageVector,
    label: String,
    unlocked: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (unlocked) {
                        Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = 0.3f),
                                color.copy(alpha = 0.1f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (unlocked) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontWeight = if (unlocked) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// --- Helper Composables ---

@Composable
fun WelcomeBanner(userName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MediumPurple.copy(alpha = 0.3f),
                            DarkPurple.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Hello, $userName!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Keep track of your progress and connect with your future.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                iconColor.copy(alpha = 0.3f),
                                iconColor.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Navigate",
                modifier = Modifier.size(24.dp),
                tint = MediumPurple
            )
        }
    }
}