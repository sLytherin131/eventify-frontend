package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.R

@Composable
fun ListEventScreen(navController: NavController) {
    val backgroundColor = Color(0xFF92B0BC)
    val navBarColor = Color(0xFF243447)
    val lightCream = Color(0xFFEEEECF)

    val items = listOf("Home", "List", "Add", "Chart", "Personal")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.List,
        Icons.Default.Add,
        Icons.Default.PieChart,
        Icons.Default.Person
    )
    var selectedIndex by remember { mutableStateOf(1) } // Index 1 = List

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            TopAppBar(
                backgroundColor = navBarColor,
                elevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.eventifylogo),
                        contentDescription = "Eventify Logo",
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Fit // atau ContentScale.FillBounds
                    )

                }
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
                            when (item) {
                                "Home" -> navController.navigate("home")
                                "List" -> navController.navigate("list_event")
                                // Tambah navigasi lainnya jika ada
                            }
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "List Event Page", fontSize = 18.sp, color = lightCream)
        }
    }
}
