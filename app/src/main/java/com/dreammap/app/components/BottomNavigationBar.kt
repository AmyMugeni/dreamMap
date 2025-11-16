package com.dreammap.app.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dreammap.app.Screen
import com.dreammap.app.ui.theme.*

@Composable
fun DreamMapBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define navigation items
    val navItems = listOf(
        NavItem(
            route = "${Screen.HomeGraph.route}/${Screen.HomeGraph.Dashboard.route}",
            label = "Home",
            icon = Icons.Filled.Home,
            selectedIcon = Icons.Filled.Home
        ),
        NavItem(
            route = "${Screen.HomeGraph.route}/${Screen.HomeGraph.Roadmaps.route}",
            label = "Roadmaps",
            icon = Icons.Filled.Timeline,
            selectedIcon = Icons.Filled.Timeline
        ),
        NavItem(
            route = "${Screen.HomeGraph.route}/${Screen.HomeGraph.Mentors.route}",
            label = "Mentors",
            icon = Icons.Filled.People,
            selectedIcon = Icons.Filled.People
        ),
        NavItem(
            route = "${Screen.HomeGraph.route}/${Screen.HomeGraph.Profile.route}",
            label = "Profile",
            icon = Icons.Filled.Person,
            selectedIcon = Icons.Filled.Person
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
    navItems.forEach { item ->
        // Only show bottom nav on main tab screens, not detail screens
        val isMainScreen = currentRoute == item.route
        val isSelected = isMainScreen

            NavigationBarItem(
                icon = {
                    if (isSelected) {
                        // Selected state with gradient background
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MediumPurple.copy(alpha = 0.3f),
                                            MediumPurple.copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.selectedIcon,
                                contentDescription = item.label,
                                tint = MediumPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) {
                            MediumPurple
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to avoid building up
                            // a large stack of destinations on the back stack as users select items
                            popUpTo(Screen.HomeGraph.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MediumPurple,
                    selectedTextColor = MediumPurple,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

