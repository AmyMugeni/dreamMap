package com.dreammap.app.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    userRepository: com.dreammap.app.data.repositories.UserRepository
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val userId = currentUser?.uid ?: return

    val quizViewModelFactory = CareerQuizViewModelFactory(userRepository)
    val quizViewModel: CareerQuizViewModel = viewModel(factory = quizViewModelFactory)

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

    // Navigate to home when quiz is completed
    LaunchedEffect(quizCompleted) {
        if (quizCompleted) {
            navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.Dashboard.route}") {
                popUpTo(Screen.AuthGraph.route) { inclusive = true }
            }
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

