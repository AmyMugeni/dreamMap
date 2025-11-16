package com.dreammap.app.screens.mentor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dreammap.app.data.model.User
import com.dreammap.app.viewmodels.MentorProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MentorProfileEditScreen(
    mentorUid: String, // Current mentor UID
    viewModel: MentorProfileViewModel,
    onSaved: () -> Unit // Callback after saving
) {
    // --- Collect State from ViewModel ---
    val mentor by viewModel.mentor.collectAsState()
    val scope = rememberCoroutineScope()

    // --- Fetch profile when the screen is first composed ---
    LaunchedEffect(key1 = mentorUid) {
        viewModel.fetchMentorProfile(mentorUid)
    }

    // --- State for editable fields ---
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }
    var expertiseList by remember { mutableStateOf<List<String>>(emptyList()) }
    var newExpertise by remember { mutableStateOf("") }
    var roadmapsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var newRoadmap by remember { mutableStateOf("") }

    // --- Update local state when mentor data is loaded ---
    LaunchedEffect(key1 = mentor) {
        mentor?.let {
            name = it.name
            role = it.role ?: ""
            bio = it.bio ?: ""
            isAvailable = it.isAvailable
            expertiseList = it.expertise
            roadmapsList = it.roadmaps
        }
    }

    // --- Show a loading indicator while fetching ---
    if (mentor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return // Stop rendering the rest of the UI
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- Name & Role ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Role / Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Bio ---
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Expertise Chips ---
        Text("Expertise:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            expertiseList.forEachIndexed { index, label ->
                AssistChip(
                    onClick = { /* optional */ },
                    label = { Text(label) },
                    trailingIcon = {
                        IconButton(onClick = {
                            // Create a new list when removing
                            expertiseList = expertiseList.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove")
                        }
                    }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newExpertise,
                onValueChange = { newExpertise = it },
                label = { Text("Add Expertise") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                if (newExpertise.isNotBlank()) {
                    // Create a new list when adding
                    expertiseList = expertiseList + newExpertise
                    newExpertise = ""
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expertise")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Roadmaps Chips ---
        Text("Roadmaps:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            roadmapsList.forEachIndexed { index, label ->
                AssistChip(
                    onClick = { /* optional */ },
                    label = { Text(label) },
                    trailingIcon = {
                        IconButton(onClick = {
                            // Create a new list when removing
                            roadmapsList = roadmapsList.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove")
                        }
                    }
                )
            }
        }


        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newRoadmap,
                onValueChange = { newRoadmap = it },
                label = { Text("Add Roadmap") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                if (newRoadmap.isNotBlank()) {
                    // Create a new list when adding
                    roadmapsList = roadmapsList + newRoadmap
                    newRoadmap = ""
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Roadmap")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Availability ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Available for Mentorship", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isAvailable,
                onCheckedChange = { isAvailable = it }
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // --- Save Button ---
        Button(
            onClick = {
                val currentMentor = mentor
                if (currentMentor != null) {
                    val updatedMentor = currentMentor.copy(
                        name = name,
                        role = role,
                        bio = bio,
                        expertise = expertiseList,
                        roadmaps = roadmapsList,
                        isAvailable = isAvailable
                    )
                    viewModel.saveMentorProfile(updatedMentor, onComplete = onSaved)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}
