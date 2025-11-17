package com.dreammap.app.screens.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dreammap.app.Screen
import com.dreammap.app.screens.auth.AuthViewModel
import com.dreammap.app.viewmodels.CareerQuizViewModel
import com.dreammap.app.viewmodels.CareerQuizViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerInterestQuizScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userRepository: com.dreammap.app.data.repositories.UserRepository,
    roadmapRepository: com.dreammap.app.data.repositories.RoadmapRepository
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val userId = currentUser?.uid ?: return

    val quizViewModelFactory = CareerQuizViewModelFactory(userRepository, roadmapRepository)
    val quizViewModel: CareerQuizViewModel = viewModel(factory = quizViewModelFactory)

    val detectedInterests by quizViewModel.detectedInterests.collectAsState()
    val recommendedRoadmaps by quizViewModel.recommendedRoadmaps.collectAsState()

    val isLoading by quizViewModel.isLoading.collectAsState()
    val errorMessage by quizViewModel.errorMessage.collectAsState()
    val quizCompleted by quizViewModel.quizCompleted.collectAsState()

    // Quiz state
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<Int, Int>() }

    // Quiz questions
    val questions = remember {
        listOf(
            "What type of problems do you enjoy solving?",
            "What work environment appeals to you most?",
            "Which activity interests you the most?",
            "What are your strongest skills?",
            "What is your primary career goal?"
        )
    }

    val options = remember {
        mapOf(
            1 to listOf(
                "Technical problems and coding challenges",
                "Business strategy and financial planning",
                "Helping people and solving health issues",
                "Creative projects and artistic expression"
            ),
            2 to listOf(
                "Fast-paced tech startup or remote work",
                "Collaborative team environment",
                "Creative studio or freelance",
                "Structured corporate environment"
            ),
            3 to listOf(
                "Building software or apps",
                "Creating art or design projects",
                "Planning and organizing events",
                "Researching and analyzing data"
            ),
            4 to listOf(
                "Logical thinking and programming",
                "Communication and leadership",
                "Creativity and design",
                "Empathy and problem-solving"
            ),
            5 to listOf(
                "Innovate and create new technology",
                "Build a successful business",
                "Make a positive impact on people's lives",
                "Express creativity and inspire others"
            )
        )
    }

    // Show results when quiz is completed
    var showResults by remember { mutableStateOf(false) }
    
    LaunchedEffect(quizCompleted) {
        if (quizCompleted && !showResults) {
            showResults = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Career Interest Quiz") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (showResults) {
                // Results Screen
                QuizResultsScreen(
                    detectedInterests = detectedInterests,
                    recommendedRoadmaps = recommendedRoadmaps,
                    onContinue = {
                        navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.Dashboard.route}") {
                            popUpTo(Screen.AuthGraph.route) { inclusive = true }
                        }
                    },
                    onRoadmapClick = { roadmapId ->
                        navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.RoadmapDetail.createRoute(roadmapId)}")
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1) / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Question
                    Text(
                        text = questions[currentQuestionIndex],
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Options
                    val questionNumber = currentQuestionIndex + 1
                    options[questionNumber]?.forEachIndexed { index, option ->
                        val isSelected = answers[questionNumber] == index

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            onClick = {
                                answers[questionNumber] = index
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 4.dp else 2.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (currentQuestionIndex > 0) {
                            OutlinedButton(
                                onClick = { currentQuestionIndex-- },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Previous")
                            }
                        }

                        Button(
                            onClick = {
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    // Submit quiz
                                    val interests = quizViewModel.processQuizAnswers(answers.toMap())
                                    quizViewModel.saveQuizResults(userId, interests)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = answers[questionNumber] != null
                        ) {
                            Text(
                                if (currentQuestionIndex < questions.size - 1) "Next" else "Submit"
                            )
                        }
                    }

                    // Error message
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizResultsScreen(
    detectedInterests: List<String>,
    recommendedRoadmaps: List<com.dreammap.app.data.model.Roadmap>,
    onContinue: () -> Unit,
    onRoadmapClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Success Icon
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Based on your answers, we've identified your career interests and found matching roadmaps for you.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Suggested Careers Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Suggested Career Interests",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Based on your quiz answers, you might be interested in:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Interest chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    detectedInterests.forEach { interest ->
                        AssistChip(
                            onClick = { },
                            enabled = false,
                            label = {
                                Text(
                                    interest,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // Recommended Roadmaps Section
        if (recommendedRoadmaps.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Recommended Roadmaps",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "We found ${recommendedRoadmaps.size} roadmap(s) that match your interests:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                recommendedRoadmaps.forEach { roadmap ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoadmapClick(roadmap.id) },
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = roadmap.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (roadmap.shortDescription.isNotEmpty()) {
                                Text(
                                    text = roadmap.shortDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (roadmap.weeklyTimeCommitment.isNotEmpty()) {
                                    Text(
                                        text = "⏱ ${roadmap.weeklyTimeCommitment}/week",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = "View roadmap",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No roadmaps found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "We couldn't find roadmaps matching your interests right now. Check back later!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Continue to Dashboard",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

