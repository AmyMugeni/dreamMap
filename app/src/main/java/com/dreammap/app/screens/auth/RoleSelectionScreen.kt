package com.dreammap.app.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen // Import the Screen sealed class

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Choose Your Role") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Are you a student seeking guidance or a mentor sharing expertise?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // --- Student Button ---
            Button(
                onClick = {
                    // Navigate to the Sign-up screen, ready for student registration
                    navController.navigate(Screen.AuthGraph.SignUp.createRoute("student"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("I am a Student", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Mentor Button ---
            Button(
                onClick = {
                    // Navigate to the Sign-up screen, ready for mentor registration
                    navController.navigate(Screen.AuthGraph.SignUp.createRoute("mentor"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("I am a Mentor", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(64.dp))

            // --- Already have account (Login) ---
            TextButton(
                onClick = {
                    navController.navigate(Screen.AuthGraph.Login.route) {
                        // Pop role selection screen off the stack when going to login
                        popUpTo(Screen.AuthGraph.route) { inclusive = true }
                    }
                },
            ) {
                Text("Already have an account? Log In")
            }
        }
    }
}