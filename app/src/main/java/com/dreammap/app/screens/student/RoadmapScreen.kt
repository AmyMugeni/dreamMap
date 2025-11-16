package com.dreammap.app.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dreammap.app.Screen
import com.dreammap.app.viewmodels.RoadmapListUiState
import com.dreammap.app.viewmodels.RoadmapViewModel
import com.dreammap.app.viewmodels.RoadmapViewModelFactory
import com.dreammap.app.data.repositories.RoadmapRepository
import com.dreammap.app.ui.theme.*
import com.dreammap.app.components.DreamMapBottomNavigation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapListScreen(
    navController: NavController,
    roadmapRepository: RoadmapRepository
) {
    val roadmapViewModel: RoadmapViewModel = viewModel(
        factory = RoadmapViewModelFactory(roadmapRepository)
    )

    val uiState by roadmapViewModel.listUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Learning Roadmaps",
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is RoadmapListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is RoadmapListUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                is RoadmapListUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.roadmaps, key = { it.id }) { roadmap ->
                            RoadmapListItem(
                                roadmap = roadmap,
                                onClick = {
                                    navController.navigate(
                                        "${Screen.HomeGraph.route}/${Screen.HomeGraph.RoadmapDetail.createRoute(roadmap.id)}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoadmapListItem(
    roadmap: com.dreammap.app.data.model.Roadmap,
    onClick: () -> Unit
) {
    val completedMilestones = roadmap.milestones.count { it.isCompleted }
    val totalMilestones = roadmap.milestones.size
    val progress = if (totalMilestones > 0) completedMilestones.toFloat() / totalMilestones else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(MediumPurple, LightPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Timeline,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roadmap.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = roadmap.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Progress indicator
                if (totalMilestones > 0) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress: $completedMilestones/$totalMilestones milestones",
                                style = MaterialTheme.typography.labelSmall,
                                color = MediumPurple,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MediumPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MediumPurple,
                            trackColor = LightPurple.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}
