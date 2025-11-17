package com.dreammap.app.data.model

import com.dreammap.app.data.model.Milestone
import com.dreammap.app.data.model.Roadmap
import com.dreammap.app.data.model.Event
import com.dreammap.app.data.model.Resource
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date

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
        ),
        events = listOf(
            Event(
                id = "web_dev_event_001",
                title = "Introduction to React Webinar",
                description = "Learn the fundamentals of React and component-based architecture in this interactive webinar.",
                eventDate = Timestamp(Date(Calendar.getInstance().apply { 
                    set(2025, Calendar.FEBRUARY, 15, 18, 0) 
                }.timeInMillis)),
                registrationUrl = "https://example.com/register/react-webinar",
                eventType = "webinar",
                isRegistered = false,
                organizer = "Tech Education Hub",
                location = "Online",
                maxParticipants = 100,
                currentParticipants = 45
            ),
            Event(
                id = "web_dev_event_002",
                title = "Full-Stack Development Workshop",
                description = "Hands-on workshop on building full-stack applications with Node.js and React.",
                eventDate = Timestamp(Calendar.getInstance().apply { 
                    set(2025, Calendar.MARCH, 1, 14, 0) 
                }.timeInMillis / 1000, 0),
                registrationUrl = "https://example.com/register/fullstack-workshop",
                eventType = "workshop",
                isRegistered = false,
                organizer = "Code Academy",
                location = "San Francisco, CA",
                maxParticipants = 50,
                currentParticipants = 30
            )
        ),
        resources = listOf(
            Resource(
                id = "web_dev_resource_001",
                title = "Complete Guide to JavaScript",
                description = "A comprehensive guide covering all JavaScript fundamentals from basics to advanced concepts.",
                resourceType = "article",
                url = "https://example.com/articles/javascript-guide",
                thumbnailUrl = null,
                durationMinutes = 0,
                author = "John Doe",
                isBookmarked = false,
                difficultyLevel = "beginner"
            ),
            Resource(
                id = "web_dev_resource_002",
                title = "React Crash Course",
                description = "Learn React in 2 hours with this comprehensive video tutorial covering hooks, state, and components.",
                resourceType = "video",
                url = "https://example.com/videos/react-crash-course",
                thumbnailUrl = "https://example.com/thumbnails/react-course.jpg",
                durationMinutes = 120,
                author = "Jane Smith",
                isBookmarked = false,
                difficultyLevel = "intermediate"
            ),
            Resource(
                id = "web_dev_resource_003",
                title = "CSS Grid Layout Tutorial",
                description = "Master CSS Grid with practical examples and real-world use cases.",
                resourceType = "tutorial",
                url = "https://example.com/tutorials/css-grid",
                thumbnailUrl = null,
                durationMinutes = 45,
                author = "Mike Johnson",
                isBookmarked = false,
                difficultyLevel = "beginner"
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
        ),
        events = listOf(
            Event(
                id = "android_dev_event_001",
                title = "Kotlin for Android Developers",
                description = "Learn how to use Kotlin effectively for Android development.",
                eventDate = Timestamp(Calendar.getInstance().apply { 
                    set(2025, Calendar.FEBRUARY, 20, 19, 0) 
                }.timeInMillis / 1000, 0),
                registrationUrl = "https://example.com/register/kotlin-android",
                eventType = "webinar",
                isRegistered = false,
                organizer = "Android Developers Community",
                location = "Online",
                maxParticipants = 200,
                currentParticipants = 120
            )
        ),
        resources = listOf(
            Resource(
                id = "android_dev_resource_001",
                title = "Kotlin Programming Language Guide",
                description = "Complete reference guide for Kotlin programming language with examples.",
                resourceType = "article",
                url = "https://example.com/articles/kotlin-guide",
                thumbnailUrl = null,
                durationMinutes = 0,
                author = "Android Team",
                isBookmarked = false,
                difficultyLevel = "beginner"
            ),
            Resource(
                id = "android_dev_resource_002",
                title = "Jetpack Compose Masterclass",
                description = "Comprehensive course on building modern Android UIs with Jetpack Compose.",
                resourceType = "course",
                url = "https://example.com/courses/jetpack-compose",
                thumbnailUrl = "https://example.com/thumbnails/compose-course.jpg",
                durationMinutes = 480,
                author = "Sarah Williams",
                isBookmarked = false,
                difficultyLevel = "intermediate"
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
        ),
        events = listOf(
            Event(
                id = "data_science_event_001",
                title = "Machine Learning Conference 2025",
                description = "Join industry experts for talks on the latest ML trends and techniques.",
                eventDate = Timestamp(Calendar.getInstance().apply { 
                    set(2025, Calendar.MARCH, 10, 9, 0) 
                }.timeInMillis / 1000, 0),
                registrationUrl = "https://example.com/register/ml-conference",
                eventType = "conference",
                isRegistered = false,
                organizer = "Data Science Institute",
                location = "New York, NY",
                maxParticipants = 500,
                currentParticipants = 350
            ),
            Event(
                id = "data_science_event_002",
                title = "Python for Data Analysis Workshop",
                description = "Hands-on workshop on using Python, Pandas, and NumPy for data analysis.",
                eventDate = Timestamp(Calendar.getInstance().apply { 
                    set(2025, Calendar.FEBRUARY, 25, 10, 0) 
                }.timeInMillis / 1000, 0),
                registrationUrl = "https://example.com/register/python-workshop",
                eventType = "workshop",
                isRegistered = false,
                organizer = "Python Data Community",
                location = "Online",
                maxParticipants = 75,
                currentParticipants = 60
            )
        ),
        resources = listOf(
            Resource(
                id = "data_science_resource_001",
                title = "Pandas Data Analysis Tutorial",
                description = "Learn how to manipulate and analyze data using Pandas library.",
                resourceType = "tutorial",
                url = "https://example.com/tutorials/pandas-analysis",
                thumbnailUrl = null,
                durationMinutes = 90,
                author = "Dr. Emily Chen",
                isBookmarked = false,
                difficultyLevel = "intermediate"
            ),
            Resource(
                id = "data_science_resource_002",
                title = "Introduction to NumPy",
                description = "Comprehensive guide to NumPy arrays and mathematical operations.",
                resourceType = "article",
                url = "https://example.com/articles/numpy-intro",
                thumbnailUrl = null,
                durationMinutes = 0,
                author = "Data Science Weekly",
                isBookmarked = false,
                difficultyLevel = "beginner"
            ),
            Resource(
                id = "data_science_resource_003",
                title = "Machine Learning Fundamentals",
                description = "Video course covering the fundamentals of machine learning algorithms.",
                resourceType = "course",
                url = "https://example.com/courses/ml-fundamentals",
                thumbnailUrl = "https://example.com/thumbnails/ml-course.jpg",
                durationMinutes = 600,
                author = "Prof. Robert Kim",
                isBookmarked = false,
                difficultyLevel = "advanced"
            )
        )
    )
    val allRoadmaps = listOf(roadmap1, roadmap2, roadmap3)
}