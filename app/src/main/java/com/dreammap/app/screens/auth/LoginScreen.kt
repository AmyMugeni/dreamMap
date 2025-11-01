package com.dreammap.app.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dreammap.app.Screen // Import your navigation routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    // NO default value; it must be passed in
    authViewModel: AuthViewModel
) {
    // Input States
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ViewModel States
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    // --- Side Effects and Navigation ---

    // 1. Navigate on successful login
    LaunchedEffect(user) {
        if (user != null) {
            // Success! AuthViewModel already determined the user's role.
            navController.navigate(Screen.HomeGraph.route) {
                // Clear the entire navigation stack up to the root
                popUpTo(Screen.AuthGraph.route) { inclusive = true }
            }
        }
    }

    // 2. Error Message Display (using a simple Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = errorMessage!!,
                actionLabel = "Dismiss"
            )
        }
    }

    // --- UI Structure ---
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Log In") },
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
            Spacer(modifier = Modifier.height(64.dp))

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
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(), // Hides characters
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = {
                    // Call the ViewModel's login function
                    authViewModel.loginUser(email, password)
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Log In")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigate to Sign Up
            TextButton(
                onClick = {
                    // Pop the current Login screen off the stack and navigate to Role Selection
                    navController.navigate(Screen.AuthGraph.RoleSelection.route) {
                        popUpTo(Screen.AuthGraph.Login.route) { inclusive = true }
                    }
                },
            ) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}