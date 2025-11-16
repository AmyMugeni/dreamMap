package com.dreammap.app.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.data.model.User
import com.dreammap.app.util.constants.FirebaseConstants
import com.dreammap.app.viewmodels.AdminViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    navController: NavHostController,
    userId: String,
    adminViewModel: AdminViewModel
) {
    val selectedUser by adminViewModel.selectedUser.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    val errorMessage by adminViewModel.errorMessage.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Dialog state variables
    var showRoleDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        adminViewModel.loadUserDetail(userId)
    }

    // Show error message if any
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                adminViewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        selectedUser?.name ?: "User Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (isLoading && selectedUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedUser != null) {
            val user = selectedUser!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // User Profile Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        user.name.firstOrNull()?.toString() ?: "?",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Role Badge
                            AssistChip(
                                onClick = { },
                                enabled = false,
                                label = {
                                    Text(
                                        user.role.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = when (user.role) {
                                        FirebaseConstants.ROLE_STUDENT -> MaterialTheme.colorScheme.tertiaryContainer
                                        FirebaseConstants.ROLE_MENTOR -> MaterialTheme.colorScheme.primaryContainer
                                        FirebaseConstants.ROLE_ADMIN -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    labelColor = when (user.role) {
                                        FirebaseConstants.ROLE_STUDENT -> MaterialTheme.colorScheme.onTertiaryContainer
                                        FirebaseConstants.ROLE_MENTOR -> MaterialTheme.colorScheme.onPrimaryContainer
                                        FirebaseConstants.ROLE_ADMIN -> MaterialTheme.colorScheme.onErrorContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            )
                        }
                    }
                }

                item {
                    // User Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "User Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            DetailRow(Icons.Filled.Email, "Email", user.email)
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(Icons.Filled.Person, "User ID", user.uid)
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(
                                Icons.Filled.CalendarToday,
                                "Date Joined",
                                formatDate(user.dateJoined)
                            )
                        }
                    }
                }

                // Role-specific information
                if (user.role == FirebaseConstants.ROLE_STUDENT) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Student Information",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                DetailRow(
                                    Icons.Filled.CheckCircle,
                                    "Quiz Completed",
                                    if (user.quizCompleted) "Yes" else "No"
                                )

                                if (user.interests.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Interests:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    user.interests.forEach { interest ->
                                        AssistChip(
                                            onClick = { },
                                            enabled = false,
                                            label = { Text(interest) },
                                            modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (user.role == FirebaseConstants.ROLE_MENTOR) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Mentor Information",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                DetailRow(
                                    Icons.Filled.Visibility,
                                    "Available",
                                    if (user.isAvailable) "Yes" else "No"
                                )

                                if (user.expertise.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Expertise:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    user.expertise.forEach { expertise ->
                                        AssistChip(
                                            onClick = { },
                                            enabled = false,
                                            label = { Text(expertise) },
                                            modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                                        )
                                    }
                                }

                                if (user.roadmaps.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Roadmaps:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    user.roadmaps.forEach { roadmap ->
                                        AssistChip(
                                            onClick = { },
                                            enabled = false,
                                            label = { Text(roadmap) },
                                            modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                                        )
                                    }
                                }

                                if (user.bio != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Bio:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = user.bio,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Admin Actions Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Admin Actions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Change Role Button
                            Button(
                                onClick = { showRoleDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Role")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Toggle Availability (for mentors only)
                            if (user.role == FirebaseConstants.ROLE_MENTOR) {
                                val currentAvailability = user.isAvailable
                                Button(
                                    onClick = {
                                        adminViewModel.toggleMentorAvailability(
                                            user.uid,
                                            !currentAvailability
                                        )
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Mentor availability ${if (!currentAvailability) "enabled" else "disabled"}"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentAvailability)
                                            MaterialTheme.colorScheme.tertiaryContainer
                                        else
                                            MaterialTheme.colorScheme.errorContainer,
                                        contentColor = if (currentAvailability)
                                            MaterialTheme.colorScheme.onTertiaryContainer
                                        else
                                            MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Icon(
                                        if (currentAvailability) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (currentAvailability) "Disable Availability" else "Enable Availability")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Delete User Button
                            Button(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete User")
                            }
                        }
                    }

                    // Role Change Dialog
                    if (showRoleDialog) {
                        AlertDialog(
                            onDismissRequest = { showRoleDialog = false },
                            title = { Text("Change User Role") },
                            text = {
                                Column {
                                    Text("Current role: ${user.role}")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Select new role:")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val roles = listOf(
                                        FirebaseConstants.ROLE_STUDENT,
                                        FirebaseConstants.ROLE_MENTOR,
                                        FirebaseConstants.ROLE_ADMIN
                                    )
                                    roles.forEach { role ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    adminViewModel.changeUserRole(user.uid, role)
                                                    showRoleDialog = false
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Role changed to $role")
                                                    }
                                                }
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = user.role == role,
                                                onClick = {
                                                    adminViewModel.changeUserRole(user.uid, role)
                                                    showRoleDialog = false
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Role changed to $role")
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                role.replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showRoleDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    // Delete Confirmation Dialog
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Delete User") },
                            text = {
                                Text("Are you sure you want to delete ${user.name}? This action cannot be undone.")
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        adminViewModel.deleteUser(user.uid) {
                                            navController.popBackStack()
                                            scope.launch {
                                                snackbarHostState.showSnackbar("User deleted successfully")
                                            }
                                        }
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "User not found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatDate(timestamp: com.google.firebase.Timestamp): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

