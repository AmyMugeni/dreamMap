package com.dreammap.app.data.model

data class MenteeRequest(
    val requestId: String,
    val menteeId: String,
    val menteeName: String,
    val menteeGoalSummary: String, // Short summary of why they requested a mentor
    val requestDate: String, // e.g., "Oct 1, 2025"
    val status: RequestStatus = RequestStatus.PENDING
)

enum class RequestStatus {
    PENDING, ACCEPTED, DECLINED
}