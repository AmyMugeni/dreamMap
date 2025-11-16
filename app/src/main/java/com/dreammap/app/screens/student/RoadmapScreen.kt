package com.dreammap.app.screens.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dreammap.app.Screen
import com.dreammap.app.viewmodels.RoadmapListUiState
import com.dreammap.app.viewmodels.RoadmapViewModel
import com.dreammap.app.viewmodels.RoadmapViewModelFactory
import com.dreammap.app.data.repositories.RoadmapRepository


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
            TopAppBar(title = { Text("Learning Roadmaps") })
        }
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
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(
                                            "${Screen.HomeGraph.route}/${Screen.HomeGraph.RoadmapDetail.createRoute(roadmap.id)}"
                                        )


                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = roadmap.title,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = roadmap.shortDescription,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
