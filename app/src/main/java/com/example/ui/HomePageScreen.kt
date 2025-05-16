package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePageScreen() {
    val backgroundColor = Color(0xFF92B0BC)
    val cardColor = Color(0xFF3D4148)
    val lightCream = Color(0xFFEEEECF)
    val navBarColor = Color(0xFF243447)

    val items = listOf("Home", "List", "Add", "Chart", "Personal")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.List,
        Icons.Default.Add,
        Icons.Default.PieChart,
        Icons.Default.Person
    )
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            TopAppBar(
                backgroundColor = navBarColor,
                elevation = 4.dp
            ) {
                Text(
                    text = "Eventify",
                    color = lightCream,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: Navigate to calendar screen
                },
                backgroundColor = navBarColor,
                contentColor = lightCream,
                modifier = Modifier.padding(bottom = 56.dp, end = 16.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar")
            }
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = navBarColor,
                contentColor = lightCream
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = item,
                                tint = if (selectedIndex == index) Color.LightGray else lightCream
                            )
                        },
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            // TODO: Navigate to respective screen
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                backgroundColor = cardColor,
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Task-list Personal",
                        color = lightCream,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    )
                    IconButton(
                        onClick = {
                            // TODO: Tambah task personal
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task", tint = lightCream)
                    }
                }
            }
        }
    }
}
