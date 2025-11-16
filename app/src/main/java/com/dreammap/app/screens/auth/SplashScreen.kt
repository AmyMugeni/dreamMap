package com.dreammap.app.screens.auth

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dreammap.app.Screen
import com.dreammap.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animations")
    
    // Logo scale animation
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )
    
    // Logo rotation for sparkle effect
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Fade in animation
    val alpha by remember {
        mutableStateOf(Animatable(0f))
    }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }
    
    // Background gradient animation
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient"
    )

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        DarkPurpleBackground,
                        DarkPurple,
                        MediumPurple.copy(alpha = 0.8f)
                    ),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated background particles/stars
        FloatingParticles()
        
        // Main content
        Column(
            modifier = Modifier
                .alpha(alpha.value)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Icon with animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Gold.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                // Main icon circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MediumPurple, LightPurple, Gold)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating sparkle stars
                    repeat(8) { index ->
                        val angle = (index * 45f + rotation) * (Math.PI / 180).toFloat()
                        val radius = 35f
                        val x = cos(angle) * radius
                        val y = sin(angle) * radius
                        
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Gold.copy(alpha = 0.8f),
                            modifier = Modifier
                                .offset(x = x.dp, y = y.dp)
                                .size(12.dp)
                        )
                    }
                    
                    // Center star
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "DreamMap Logo",
                        tint = White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name with gradient text effect
            Text(
                text = "DreamMap",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.displayMedium,
                color = White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = "Chart Your Path to Success",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = LightPurple.copy(alpha = 0.9f),
                modifier = Modifier.alpha(0.9f)
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Gold,
                strokeWidth = 3.dp,
                trackColor = LightPurple.copy(alpha = 0.3f)
            )
        }
    }

    // --- Navigation Logic ---
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        // Show splash for at least 3.5 seconds for visual appeal
        delay(3500)

        // Check if user is already logged in
        if (currentUser != null) {
            // User is logged in, navigate based on role
            when (currentUser?.role) {
                "mentor" -> {
                    Log.d("SplashScreen", "User is mentor, navigating to Mentor Dashboard")
                    navController.navigate(
                        Screen.MentorGraph.createRoute(
                            currentUser!!.uid,
                            currentUser!!.name
                        )
                    ) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
                "admin" -> {
                    Log.d("SplashScreen", "User is admin, navigating to Admin Dashboard")
                    navController.navigate("${Screen.AdminGraph.route}/${Screen.AdminGraph.Dashboard.route}") {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
                else -> {
                    Log.d("SplashScreen", "User is student, navigating to Student Dashboard")
                    navController.navigate("${Screen.HomeGraph.route}/${Screen.HomeGraph.Dashboard.route}") {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        } else {
            // No user logged in, go to role selection
            Log.d("SplashScreen", "No user logged in, navigating to Role Selection")
            navController.navigate(Screen.AuthGraph.RoleSelection.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }
}

@Composable
fun FloatingParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    // Create multiple floating particles
    repeat(15) { index ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = -100f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + (index * 200),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_y_$index"
        )
        
        val offsetX by infiniteTransition.animateFloat(
            initialValue = (index % 5) * 100f - 200f,
            targetValue = (index % 5) * 100f - 200f + 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + (index * 150),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_x_$index"
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500 + (index * 100),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_alpha_$index"
        )
        
        val size = (20 + (index % 3) * 10).dp
        
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size(size)
                .alpha(alpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            when (index % 3) {
                                0 -> Gold.copy(alpha = 0.6f)
                                1 -> LightPurple.copy(alpha = 0.5f)
                                else -> Pink.copy(alpha = 0.4f)
                            },
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}
