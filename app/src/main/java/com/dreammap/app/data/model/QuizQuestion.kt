package com.dreammap.app.data.model

/**
 * Data model for a quiz question.
 * Each question has multiple choice options that map to different career interests.
 */
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<QuizOption>
)

data class QuizOption(
    val text: String,
    val interests: List<String> // Interests this option contributes to
)

/**
 * Predefined quiz questions for career interest assessment.
 */
object QuizQuestions {
    val questions = listOf(
        QuizQuestion(
            id = "q1",
            question = "What type of work environment do you prefer?",
            options = listOf(
                QuizOption("Working independently on projects", listOf("Logic", "Data", "Backend")),
                QuizOption("Collaborating with a creative team", listOf("UI/UX", "Design", "Frontend")),
                QuizOption("Solving complex problems with data", listOf("Data", "Machine Learning", "Backend")),
                QuizOption("Building user-friendly interfaces", listOf("UI/UX", "Frontend", "Design"))
            )
        ),
        QuizQuestion(
            id = "q2",
            question = "Which activity interests you most?",
            options = listOf(
                QuizOption("Designing beautiful websites and apps", listOf("UI/UX", "Design", "Frontend")),
                QuizOption("Writing code to solve problems", listOf("Logic", "Backend", "Web")),
                QuizOption("Analyzing data and finding patterns", listOf("Data", "Machine Learning")),
                QuizOption("Creating mobile applications", listOf("Mobile", "Android", "Frontend"))
            )
        ),
        QuizQuestion(
            id = "q3",
            question = "What skills do you enjoy using?",
            options = listOf(
                QuizOption("Visual design and creativity", listOf("Design", "UI/UX", "Frontend")),
                QuizOption("Logical thinking and algorithms", listOf("Logic", "Backend", "Data")),
                QuizOption("Communication and user research", listOf("UI/UX", "Design")),
                QuizOption("Mathematics and statistics", listOf("Data", "Machine Learning"))
            )
        ),
        QuizQuestion(
            id = "q4",
            question = "What type of projects excite you?",
            options = listOf(
                QuizOption("Building interactive websites", listOf("Web", "Frontend", "UI/UX")),
                QuizOption("Creating server systems and APIs", listOf("Backend", "Logic", "Web")),
                QuizOption("Developing mobile apps", listOf("Mobile", "Android", "Frontend")),
                QuizOption("Working with AI and machine learning", listOf("Machine Learning", "Data"))
            )
        ),
        QuizQuestion(
            id = "q5",
            question = "How do you prefer to learn?",
            options = listOf(
                QuizOption("Hands-on projects and building", listOf("Web", "Frontend", "Mobile")),
                QuizOption("Deep theoretical understanding", listOf("Data", "Machine Learning", "Backend")),
                QuizOption("Visual and creative exploration", listOf("Design", "UI/UX")),
                QuizOption("Problem-solving challenges", listOf("Logic", "Backend", "Data"))
            )
        ),
        QuizQuestion(
            id = "q6",
            question = "What career outcome appeals to you most?",
            options = listOf(
                QuizOption("Creating products people love to use", listOf("UI/UX", "Design", "Frontend")),
                QuizOption("Building scalable systems and infrastructure", listOf("Backend", "Logic", "Web")),
                QuizOption("Developing innovative mobile experiences", listOf("Mobile", "Android", "Frontend")),
                QuizOption("Unlocking insights from data", listOf("Data", "Machine Learning"))
            )
        )
    )
}

