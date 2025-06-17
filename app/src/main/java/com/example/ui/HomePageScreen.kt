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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
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
                    .padding(end = 10.dp, bottom = 10.dp) // mepet kanan bawah
                    .size(56.dp) // bentuk persegi
                    .clip(RoundedCornerShape(8.dp)) // sudut sedikit rounded, bukan bulat
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
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) // <- bentuk sudut bulat
                    .background(color = navBarColor), // <- background tanpa shape di sini
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

                        val backgroundShape = when (index) {
                            2 -> CircleShape // Tombol "+"
                            else -> RoundedCornerShape(12.dp)
                        }

                        val backgroundColor = if (isSelected) Color.White else Color.Transparent
                        val iconTint = if (isSelected) navBarColor else lightCream

                        Box(
                            modifier = Modifier
                                .size(if (index == 2) 56.dp else 48.dp)
                                .clip(backgroundShape) // <- ini untuk bentuk
                                .background(color = backgroundColor) // <- ini hanya warna
                                .clickable {
                                    selectedIndex = index
                                    when (index) {
                                        1 -> navController.navigate("list_event")
                                        2 -> navController.navigate("create_event")
                                        4 -> navController.navigate("personal_admin")
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
                        taskViewModel.tasks.forEach { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = task.isDone,
                                    onCheckedChange = { taskViewModel.toggleDone(task.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = lightCream,
                                        uncheckedColor = lightCream
                                    )
                                )
                                Text(
                                    text = task.description,
                                    color = lightCream,
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
