# Firestore Data Structure for Events & Webinars and Resource Library

This document explains how to add Events & Webinars and Resource Library data to roadmaps in Firestore, similar to how `recommended_interests` are stored.

## Roadmap Document Structure

In Firestore, each roadmap document in the `roadmaps` collection can now include:

1. **`events`** - An array of event objects
2. **`resources`** - An array of resource objects

## Events & Webinars Structure

Each event object should have the following fields:

```json
{
  "id": "event_001",
  "title": "Introduction to React Webinar",
  "description": "Learn the fundamentals of React and component-based architecture",
  "event_date": "2025-02-15T18:00:00Z", // Firestore Timestamp
  "registration_url": "https://example.com/register/react-webinar",
  "event_type": "webinar", // Options: "webinar", "workshop", "conference", "networking"
  "is_registered": false,
  "organizer": "Tech Education Hub",
  "location": "Online",
  "max_participants": 100,
  "current_participants": 45
}
```

### Example Firestore Document with Events:

```json
{
  "id": "web_dev",
  "title": "Web Development Roadmap",
  "short_description": "Master modern web development",
  "weekly_time_commitment": "10 hours",
  "recommended_interests": ["Web", "Frontend", "Backend"],
  "skills_required": ["HTML", "CSS", "JavaScript"],
  "events": [
    {
      "id": "event_001",
      "title": "Introduction to React Webinar",
      "description": "Learn React fundamentals",
      "event_date": "2025-02-15T18:00:00Z",
      "registration_url": "https://example.com/register",
      "event_type": "webinar",
      "is_registered": false,
      "organizer": "Tech Education Hub",
      "location": "Online",
      "max_participants": 100,
      "current_participants": 45
    },
    {
      "id": "event_002",
      "title": "Full-Stack Workshop",
      "description": "Hands-on workshop on building full-stack applications",
      "event_date": "2025-03-01T14:00:00Z",
      "registration_url": "https://example.com/register/workshop",
      "event_type": "workshop",
      "is_registered": false,
      "organizer": "Code Academy",
      "location": "San Francisco, CA",
      "max_participants": 50,
      "current_participants": 30
    }
  ]
}
```

## Resource Library Structure

Each resource object should have the following fields:

```json
{
  "id": "resource_001",
  "title": "Complete Guide to JavaScript",
  "description": "A comprehensive guide covering all JavaScript fundamentals",
  "resource_type": "article", // Options: "article", "video", "course", "tutorial"
  "url": "https://example.com/articles/javascript-guide",
  "thumbnail_url": "https://example.com/thumbnails/js-guide.jpg", // Optional
  "duration_minutes": 0, // For articles, use 0. For videos/courses, specify duration
  "author": "John Doe",
  "is_bookmarked": false,
  "difficulty_level": "beginner" // Options: "beginner", "intermediate", "advanced"
}
```

### Example Firestore Document with Resources:

```json
{
  "id": "web_dev",
  "title": "Web Development Roadmap",
  "short_description": "Master modern web development",
  "weekly_time_commitment": "10 hours",
  "recommended_interests": ["Web", "Frontend", "Backend"],
  "skills_required": ["HTML", "CSS", "JavaScript"],
  "resources": [
    {
      "id": "resource_001",
      "title": "Complete Guide to JavaScript",
      "description": "A comprehensive guide covering all JavaScript fundamentals",
      "resource_type": "article",
      "url": "https://example.com/articles/javascript-guide",
      "thumbnail_url": "https://example.com/thumbnails/js-guide.jpg",
      "duration_minutes": 0,
      "author": "John Doe",
      "is_bookmarked": false,
      "difficulty_level": "beginner"
    },
    {
      "id": "resource_002",
      "title": "React Tutorial Series",
      "description": "Learn React from scratch with hands-on projects",
      "resource_type": "video",
      "url": "https://youtube.com/watch?v=react-tutorial",
      "thumbnail_url": "https://example.com/thumbnails/react-series.jpg",
      "duration_minutes": 120,
      "author": "Jane Smith",
      "is_bookmarked": false,
      "difficulty_level": "intermediate"
    },
    {
      "id": "resource_003",
      "title": "Advanced Node.js Course",
      "description": "Master server-side JavaScript with Node.js",
      "resource_type": "course",
      "url": "https://example.com/courses/nodejs-advanced",
      "thumbnail_url": "https://example.com/thumbnails/nodejs-course.jpg",
      "duration_minutes": 480,
      "author": "Tech Academy",
      "is_bookmarked": false,
      "difficulty_level": "advanced"
    }
  ]
}
```

## Complete Example Roadmap Document

Here's a complete example showing all fields including events and resources:

```json
{
  "id": "web_dev",
  "title": "Web Development Roadmap",
  "short_description": "Master modern web development with React, Node.js, and more",
  "weekly_time_commitment": "10 hours",
  "recommended_interests": ["Web", "Frontend", "Backend"],
  "skills_required": ["HTML", "CSS", "JavaScript", "React", "Node.js"],
  "milestones": [
    {
      "id": "m1",
      "title": "HTML & CSS Basics",
      "description": "Learn the fundamentals of web markup and styling",
      "estimated_days": 7,
      "tasks": [
        "Learn HTML tags",
        "Master CSS selectors",
        "Build a static page"
      ],
      "is_completed": false
    }
  ],
  "events": [
    {
      "id": "event_001",
      "title": "Introduction to React Webinar",
      "description": "Learn React fundamentals in this interactive webinar",
      "event_date": "2025-02-15T18:00:00Z",
      "registration_url": "https://example.com/register/react-webinar",
      "event_type": "webinar",
      "is_registered": false,
      "organizer": "Tech Education Hub",
      "location": "Online",
      "max_participants": 100,
      "current_participants": 45
    }
  ],
  "resources": [
    {
      "id": "resource_001",
      "title": "Complete Guide to JavaScript",
      "description": "A comprehensive guide covering all JavaScript fundamentals",
      "resource_type": "article",
      "url": "https://example.com/articles/javascript-guide",
      "thumbnail_url": "https://example.com/thumbnails/js-guide.jpg",
      "duration_minutes": 0,
      "author": "John Doe",
      "is_bookmarked": false,
      "difficulty_level": "beginner"
    }
  ]
}
```

## Notes

1. **Arrays in Firestore**: Both `events` and `resources` are stored as arrays within the roadmap document, similar to `recommended_interests` and `milestones`.

2. **Field Naming**: Use snake_case for Firestore field names (e.g., `event_date`, `registration_url`) as defined in the data models with `@PropertyName` annotations.

3. **Timestamps**: Use Firestore Timestamp type for `event_date` fields.

4. **Optional Fields**: Some fields like `thumbnail_url` are optional and can be omitted if not available.

5. **User-Specific Fields**: Fields like `is_registered` and `is_bookmarked` are user-specific and should be managed per user, not in the roadmap document itself. Consider storing these in a separate user-specific collection.

6. **Empty Arrays**: If a roadmap has no events or resources, you can either omit the field or set it to an empty array `[]`.

## Implementation in Code

The data models (`Event.kt` and `Resource.kt`) are already set up to automatically map these Firestore fields to Kotlin data classes. The `Roadmap.kt` model includes:

- `@PropertyName("events") var events: List<Event> = emptyList()`
- `@PropertyName("resources") var resources: List<Resource> = emptyList()`

The UI components in `RoadmapDetailScreen.kt` will automatically display these sections when the data is present in the roadmap.
