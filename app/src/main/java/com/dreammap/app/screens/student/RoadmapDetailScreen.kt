package com.dreammap.app.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dreammap.app.data.model.Milestone
import com.dreammap.app.data.model.Roadmap
import com.dreammap.app.viewmodels.RoadmapDetailUiState
import com.dreammap.app.viewmodels.RoadmapViewModel

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
                title = { Text("Your Learning Roadmap") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
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
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = roadmap.shortDescription,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Commitment: ${roadmap.weeklyTimeCommitment}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Skills: ${roadmap.skillsRequired.joinToString(", ")}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = milestone.isCompleted,
                    onCheckedChange = { checked -> onToggle(milestone.id, checked) },
                    modifier = Modifier.padding(end = 8.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Est. ${milestone.estimatedDays} days",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector =
                            if (isExpanded) Icons.Filled.ExpandLess
                            else Icons.Filled.ExpandMore,
                        contentDescription =
                            if (isExpanded) "Collapse" else "Expand"
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
