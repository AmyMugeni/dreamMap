package com.dreammap.app.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dreammap.app.data.model.Milestone
import com.dreammap.app.data.model.Roadmap
import com.dreammap.app.viewmodels.RoadmapDetailUiState
import com.dreammap.app.viewmodels.RoadmapViewModel
import com.dreammap.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapDetailScreen(
    navController: NavController,
    roadmapId: String,
    roadmapViewModel: RoadmapViewModel = viewModel()
) {
    // Load the roadmap by ID
    LaunchedEffect(roadmapId) {
        roadmapViewModel.loadRoadmapDetail(roadmapId)
    }

    // Use the correct StateFlow from ViewModel
    val uiState by roadmapViewModel.detailUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Your Learning Roadmap",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                RoadmapDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is RoadmapDetailUiState.Error -> {
                    Text(
                        text = "Error loading roadmap: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }

                is RoadmapDetailUiState.Success -> {
                    RoadmapContent(
                        roadmap = state.roadmap,
                        onMilestoneToggle = roadmapViewModel::toggleMilestoneCompletion
                    )
                }
            }
        }
    }
}


@Composable
fun RoadmapContent(
    roadmap: Roadmap,
    onMilestoneToggle: (String, Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            RoadmapHeader(roadmap)
            Spacer(modifier = Modifier.height(16.dp))
            // Gamification: Overall Progress
            RoadmapProgressCard(roadmap = roadmap)
            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Milestones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(roadmap.milestones, key = { it.id }) { milestone ->
            MilestoneCard(milestone = milestone, onToggle = onMilestoneToggle)
        }
    }
}

@Composable
fun RoadmapHeader(roadmap: Roadmap) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = roadmap.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = DarkPurple
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = roadmap.shortDescription,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Commitment badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LightPurple.copy(alpha = 0.2f),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "⏱ ${roadmap.weeklyTimeCommitment}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MediumPurple,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            // Skills badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LightPurple.copy(alpha = 0.2f),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "🎯 ${roadmap.skillsRequired.size} skills",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MediumPurple,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RoadmapProgressCard(roadmap: Roadmap) {
    val completedCount = roadmap.milestones.count { it.isCompleted }
    val totalCount = roadmap.milestones.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val xpEarned = completedCount * 50 // 50 XP per milestone

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Roadmap Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$completedCount of $totalCount milestones completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                // XP Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Gold, LightGold)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "XP",
                            tint = DarkPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "+$xpEarned",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkPurple
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MediumPurple,
                trackColor = LightPurple.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(progress * 100).toInt()}% Complete",
                style = MaterialTheme.typography.labelMedium,
                color = MediumPurple,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MilestoneCard(
    milestone: Milestone,
    onToggle: (String, Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(milestone.isCompleted) }
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (milestone.isCompleted) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isCompleted) {
                LightPurple.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gamified Checkbox with XP indicator
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    Checkbox(
                        checked = milestone.isCompleted,
                        onCheckedChange = { checked -> onToggle(milestone.id, checked) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MediumPurple,
                            checkmarkColor = White
                        )
                    )
                    // XP badge when completed
                    if (milestone.isCompleted) {
                        Box(
                            modifier = Modifier
                                .offset(x = 24.dp, y = (-8).dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Gold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "XP Earned",
                                tint = DarkPurple,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (milestone.isCompleted) MediumPurple else MaterialTheme.colorScheme.onSurface
                        )
                        if (milestone.isCompleted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Completed",
                                tint = Gold.copy(alpha = glowAlpha),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Est. ${milestone.estimatedDays} days",
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (milestone.isCompleted) {
                            Text(
                                text = "• +50 XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector =
                            if (isExpanded) Icons.Filled.ExpandLess
                            else Icons.Filled.ExpandMore,
                        contentDescription =
                            if (isExpanded) "Collapse" else "Expand",
                        tint = if (milestone.isCompleted) MediumPurple else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = milestone.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    milestone.tasks.forEach { task ->
                        Text(
                            text = "• $task",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
