package com.dreammap.app.data.model

data class Mentor(
    val id: String,
    val name: String,
    val expertise: String, // e.g., "React Development", "Product Strategy"
    val bioSummary: String,
    val rating: Double, // Average rating by students
    val focusRoadmapIds: List<String> // Roadmaps they can mentor on
)

// Example data for the screen
val sampleMentors = listOf(
    Mentor("m1", "Dr. Alex Njoroge", "Frontend Dev & UX", "10+ years experience building scalable web apps.", 4.8, listOf("1")),
    Mentor("m2", "Jon Snow", "Business & Strategy", "Agile coach and former startup founder.", 4.9, listOf("2")),
    Mentor("m3", "Markus Aurelius", "Data Science/ML", "Specializes in Python and neural networks.", 4.5, listOf("3")),
)