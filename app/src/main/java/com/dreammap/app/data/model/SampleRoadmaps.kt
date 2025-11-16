package com.dreammap.app.data.model


import com.dreammap.app.data.model.Milestone
import com.dreammap.app.data.model.Roadmap

object StaticRoadmapData {
    val initialRoadmap = Roadmap(
        id = "current_curriculum",
        title = "Frontend Dev Mastery: React & State",
        shortDescription = "Learn React, Vue, and modern component architecture.",
        weeklyTimeCommitment = "5 hours",
        recommendedInterests = listOf("UI/UX", "Logic", "Design"),
        skillsRequired = listOf("JavaScript", "HTML/CSS", "Git"),
        milestones = listOf(
            Milestone(
                id = "m1",
                title = "1. Setup & Foundations",
                description = "Install necessary tools and understand basic web architecture.",
                estimatedDays = 5,
                tasks = listOf("Install VS Code", "Learn basic HTML tags", "Push first static page to GitHub"),
                isCompleted = true
            ),
            Milestone(
                id = "m2",
                title = "2. Component Architecture",
                description = "Master React functional components and state management.",
                estimatedDays = 15,
                tasks = listOf("Understand useEffect and cleanup", "Build a small portfolio site with React", "Use conditional rendering and lists"),
                isCompleted = false
            ),
            Milestone(
                id = "m3",
                title = "3. State Management Deep Dive",
                description = "Deep dive into Redux or Context API for global state.",
                estimatedDays = 10,
                tasks = listOf("Set up a global Context store", "Refactor the portfolio site to use global state", "Research Zustand for comparison"),
                isCompleted = false
            )
        )
    )
    val roadmap1 = Roadmap(
        id = "web_dev",
        title = "Web Development Roadmap",
        shortDescription = "Learn to build modern web applications from scratch.",
        weeklyTimeCommitment = "5-7 hours",
        recommendedInterests = listOf("Web", "Frontend", "Backend"),
        skillsRequired = listOf("HTML", "CSS", "JavaScript"),
        milestones = listOf(
            Milestone(
                id = "html_css",
                title = "HTML & CSS Basics",
                description = "Learn the structure of web pages and styling.",
                estimatedDays = 5,
                tasks = listOf("Learn HTML tags", "Practice CSS layouts", "Build a simple webpage")
            ),
            Milestone(
                id = "js_basics",
                title = "JavaScript Basics",
                description = "Understand JS fundamentals and DOM manipulation.",
                estimatedDays = 7,
                tasks = listOf("Variables & Data Types", "Functions", "Events & DOM")
            )
        )
    )
    val roadmap2 = Roadmap(
        id = "android_dev",
        title = "Android Development Roadmap",
        shortDescription = "Become an Android app developer using Kotlin.",
        weeklyTimeCommitment = "6-8 hours",
        recommendedInterests = listOf("Android", "Mobile"),
        skillsRequired = listOf("Kotlin", "Jetpack Compose"),
        milestones = listOf(
            Milestone(
                id = "kotlin_basics",
                title = "Kotlin Basics",
                description = "Learn the core Kotlin programming language.",
                estimatedDays = 4,
                tasks = listOf("Variables & Types", "Control Flow", "Functions & OOP")
            ),
            Milestone(
                id = "compose_ui",
                title = "Jetpack Compose UI",
                description = "Build Android UIs with Compose.",
                estimatedDays = 6,
                tasks = listOf("Layouts", "State management", "Navigation")
            )
        )
    )
    val roadmap3 = Roadmap(
        id = "data_science",
        title = "Data Science Roadmap",
        shortDescription = "Learn data analysis, visualization, and machine learning.",
        weeklyTimeCommitment = "7-10 hours",
        recommendedInterests = listOf("Data", "Machine Learning"),
        skillsRequired = listOf("Python", "Pandas", "NumPy"),
        milestones = listOf(
            Milestone(
                id = "python_basics",
                title = "Python Basics",
                description = "Learn Python syntax and basics for data manipulation.",
                estimatedDays = 5,
                tasks = listOf("Data types", "Loops & Conditions", "Functions & Modules")
            ),
            Milestone(
                id = "ml_intro",
                title = "Intro to Machine Learning",
                description = "Understand ML concepts and build simple models.",
                estimatedDays = 8,
                tasks = listOf("Linear Regression", "Classification", "Model Evaluation")
            )
        )
    )
    val allRoadmaps = listOf(roadmap1, roadmap2, roadmap3)
}