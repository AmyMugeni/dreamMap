package com.dreammap.app.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dreammap.app.Screen // Import your navigation routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    // The role is passed as a Nav Argument from RoleSelectionScreen
    role: String? = navController.currentBackStackEntry?.arguments?.getString("role")
) {
    // Input States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ViewModel States
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    // --- Side Effects and Navigation ---

    // 1. Navigate on successful sign-up
    LaunchedEffect(user) {
        if (user != null) {
            // Success! The AuthViewModel has already resolved the user's role.
            navController.navigate(Screen.HomeGraph.route) {
                // Clear the Auth stack
                popUpTo(Screen.AuthGraph.route) { inclusive = true }
            }
        }
    }

    // 2. Display error messages
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // In a real app, you'd show a Snackbar here
            println("Sign-up Error: $errorMessage")
        }
    }

    // --- UI Structure ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up as a ${role?.replaceFirstChar { it.uppercase() } ?: "User"}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (6+ characters)") },
                modifier = Modifier.fillMaxWidth()
                // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) // Add this later
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Button
            Button(
                onClick = {
                    if (role != null && password.length >= 6) {
                        // Call the ViewModel's registration function
                        authViewModel.registerUser(email, password, name, role)
                    } else {
                        // Handle validation errors locally
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create Account")
                }
            }

            // Error Display
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}