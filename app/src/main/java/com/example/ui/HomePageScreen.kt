package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R

@Composable
fun HomePageScreen(
    navController: NavController,
    jwtToken: String,
    taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(jwtToken))
) {
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
    var showDialog by remember { mutableStateOf(false) }

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
                        contentScale = ContentScale.Fit
                    )
                }
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 10.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = navBarColor)
                    .clickable { navController.navigate("calendar") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Calendar",
                    tint = lightCream
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(color = navBarColor),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = selectedIndex == index
                        val backgroundShape = if (index == 2) CircleShape else RoundedCornerShape(12.dp)
                        val backgroundColor = if (isSelected) Color.White else Color.Transparent
                        val iconTint = if (isSelected) navBarColor else lightCream

                        Box(
                            modifier = Modifier
                                .size(if (index == 2) 56.dp else 48.dp)
                                .clip(backgroundShape)
                                .background(backgroundColor)
                                .clickable {
                                    selectedIndex = index
                                    when (index) {
                                        1 -> navController.navigate("list_event")
                                        2 -> navController.navigate("create_event/$jwtToken")
                                        3 -> navController.navigate("chart_page")
                                        4 -> navController.navigate("personal_admin/${jwtToken}")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = item,
                                tint = iconTint
                            )
                        }
                    }
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Task-list Personal",
                            color = lightCream,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = lightCream)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (taskViewModel.tasks.isEmpty()) {
                        Text("No personal tasks yet.", color = lightCream)
                    } else {
                        fun getTypeColor(type: String): Color {
                            return when (type) {
                                "Personal" -> Color(0xFFB5E48C)
                                "Work" -> Color(0xFFFFF59D)
                                "Urgent" -> Color(0xFFE57373)
                                else -> lightCream
                            }
                        }

                        taskViewModel.tasks.forEach { task ->
                            val typeColor = getTypeColor(task.taskType)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Task Icon",
                                    tint = typeColor
                                )
                                Text(
                                    text = task.description,
                                    color = typeColor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                )
                                IconButton(onClick = { taskViewModel.deleteTask(task.id) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = lightCream
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddPersonalTaskDialog(
            onDismiss = { showDialog = false },
            onAddTask = { desc, type ->
                taskViewModel.addTask(desc, type)
                showDialog = false
            }
        )
    }
}
