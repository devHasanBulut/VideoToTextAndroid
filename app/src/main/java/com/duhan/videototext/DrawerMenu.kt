package com.duhan.videototext

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DrawerMenu(
    isOpen: Boolean,
    onClose: () -> Unit,
    navController: NavController,
    onAddCategoryClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF0F4F8), Color(0xFF6dd5ed))
                    ),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Menu",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Menu", tint = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                NavigationDrawerItem(
                    label = { Text("Favorites", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("favorites")
                        onClose()
                    },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites", tint = Color.Black) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Settings", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("Settings")
                        onClose()
                    },
                    icon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Downloads", tint = Color.Black) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Downloads", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("downloads")
                        onClose()
                    },
                    icon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Downloads", tint = Color.Black) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Add Category", color = Color.Black) },
                    selected = false,
                    onClick = onAddCategoryClick,
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add Category", tint = Color.Black) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                )

            }
        }
    }
}