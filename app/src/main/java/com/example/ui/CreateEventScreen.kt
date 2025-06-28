package com.example.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.app.R
import java.text.SimpleDateFormat
import java.util.*

enum class TaskType {
    NORMAL, URGENT
}

@Composable
fun CreateEventScreen(
    navController: NavController,
    jwtToken: String
) {
    val backgroundColor = Color(0xFF92B0BC)
    val cardColor = Color(0xFF1F2E43)
    val lightCream = Color(0xFFEEEECF)
    val navBarColor = Color(0xFF243447)

    var eventName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var timeStart by remember { mutableStateOf("Sun, 20 April 2025 09:30") }
    var timeEnd by remember { mutableStateOf("Sun, 20 April 2025 12:00") }
    var searchMember by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(2) }
    var showEditTaskDialog by remember { mutableStateOf(false) }
    var editedTaskIndex by remember { mutableStateOf(-1) }
    var editedTaskMemo by remember { mutableStateOf("") }
    var editedTaskType by remember { mutableStateOf(TaskType.NORMAL) }

    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.Add, Icons.Default.PieChart, Icons.Default.Person)
    val items = listOf("Home", "List", "Create", "Settings", "Account")

    val dummyMembers = remember {
        mutableStateListOf("Name - Whatsapp Number")
    }

    val dummyTasks = remember {
        mutableStateListOf<Pair<String, TaskType>>()
    }

    var showTaskDialog by remember { mutableStateOf(false) }
    var newTaskMemo by remember { mutableStateOf("") }
    var newTaskType by remember { mutableStateOf(TaskType.NORMAL) }

    val context = LocalContext.current

    fun pickDateTime(onPicked: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            TimePickerDialog(context, { _: TimePicker, hour: Int, minute: Int ->
                calendar.set(year, month, dayOfMonth, hour, minute)
                val date = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale.getDefault()).format(calendar.time)
                onPicked(date)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            TopAppBar(backgroundColor = navBarColor, elevation = 4.dp) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.eventifylogo),
                        contentDescription = "Eventify Logo",
                        modifier = Modifier.size(150.dp)
                    )
                }
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                                        0 -> navController.navigate("home")
                                        1 -> navController.navigate("list_event")
                                        2 -> navController.navigate("create_event")
                                        3 -> navController.navigate("chart_page")
                                        4 -> navController.navigate("personal_admin")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icons[index], contentDescription = item, tint = iconTint)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    backgroundColor = cardColor,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Create Event", color = lightCream, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = eventName,
                            onValueChange = { eventName = it },
                            label = { Text("Event Name:", color = lightCream) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.LightGray)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description:", color = lightCream) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.LightGray)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Time Start:", color = lightCream)
                        OutlinedTextField(
                            value = timeStart,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth().clickable { pickDateTime { timeStart = it } },
                            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = Color.White, disabledTextColor = Color.White)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Time End:", color = lightCream)
                        OutlinedTextField(
                            value = timeEnd,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth().clickable { pickDateTime { timeEnd = it } },
                            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = Color.White, disabledTextColor = Color.White)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Add Member:", color = lightCream, modifier = Modifier.weight(1f))
                            OutlinedTextField(
                                value = searchMember,
                                onValueChange = { searchMember = it },
                                placeholder = { Text("Search...", fontSize = 12.sp, color = Color.LightGray) },
                                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = lightCream) },
                                modifier = Modifier.weight(2f),
                                colors = TextFieldDefaults.outlinedTextFieldColors(textColor = Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(backgroundColor = Color.White, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Members", color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                                Spacer(modifier = Modifier.height(4.dp))
                                dummyMembers.forEachIndexed { index, member ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                        Text("• $member", modifier = Modifier.weight(1f))
                                        IconButton(onClick = { dummyMembers.removeAt(index) }) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("Add Task:", color = lightCream, modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                                IconButton(
                                    onClick = { showTaskDialog = true },
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color.White)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Task", tint = cardColor)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        dummyTasks.forEachIndexed { index, task ->
                            val (memo, type) = task
                            val taskColor = if (type == TaskType.NORMAL) Color(0xFFB89B1F) else Color(0xFF9B2C40)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(taskColor)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("${index + 1}. ${memo.take(25)}...", color = Color.White, modifier = Modifier.weight(1f))

                                // ✅ Tombol Edit dengan aksi menampilkan dialog edit
                                IconButton(onClick = {
                                    editedTaskIndex = index
                                    editedTaskMemo = memo
                                    editedTaskType = type
                                    showEditTaskDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }

                                IconButton(onClick = { dummyTasks.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { /* Create logic */ },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6A8695), contentColor = Color.White),
                            modifier = Modifier.align(Alignment.CenterHorizontally).width(130.dp),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Create")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showTaskDialog) {
        AlertDialog(
            onDismissRequest = { showTaskDialog = false },
            confirmButton = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                            if (newTaskMemo.isNotBlank()) {
                                dummyTasks.add(Pair(newTaskMemo, newTaskType))
                                newTaskMemo = ""
                                newTaskType = TaskType.NORMAL
                                showTaskDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF5E819B),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.width(130.dp)
                    ) {
                        Text("Add")
                    }
                }
            },
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Task", color = Color(0xFFEEEECF), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newTaskMemo,
                        onValueChange = { newTaskMemo = it },
                        placeholder = { Text("Memo...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            backgroundColor = Color.White,
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Type:", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol NORMAL
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF6A5F00))
                                .clickable { newTaskType = TaskType.NORMAL }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = newTaskType == TaskType.NORMAL,
                                onClick = { newTaskType = TaskType.NORMAL },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White
                                )
                            )
                            Text("Normal", color = Color.White)
                        }

                        // Tombol URGENT
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF661D1D))
                                .clickable { newTaskType = TaskType.URGENT }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = newTaskType == TaskType.URGENT,
                                onClick = { newTaskType = TaskType.URGENT },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White
                                )
                            )
                            Text("Urgent", color = Color.White)
                        }
                    }
                }
            },
            backgroundColor = Color(0xFF1F2E43),
            contentColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
    }
    if (showEditTaskDialog && editedTaskIndex >= 0) {
        AlertDialog(
            onDismissRequest = { showEditTaskDialog = false },
            confirmButton = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                            if (editedTaskMemo.isNotBlank()) {
                                dummyTasks[editedTaskIndex] = Pair(editedTaskMemo, editedTaskType)
                                showEditTaskDialog = false
                                editedTaskIndex = -1
                                editedTaskMemo = ""
                                editedTaskType = TaskType.NORMAL
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF5E819B),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.width(130.dp)
                    ) {
                        Text("Save")
                    }
                }
            },
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Task", color = Color(0xFFEEEECF), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editedTaskMemo,
                        onValueChange = { editedTaskMemo = it },
                        placeholder = { Text("Memo...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            backgroundColor = Color.White,
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Type:", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol NORMAL
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF6A5F00))
                                .clickable { editedTaskType = TaskType.NORMAL }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = editedTaskType == TaskType.NORMAL,
                                onClick = { editedTaskType = TaskType.NORMAL },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White
                                )
                            )
                            Text("Normal", color = Color.White)
                        }

                        // Tombol URGENT
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF661D1D))
                                .clickable { editedTaskType = TaskType.URGENT }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = editedTaskType == TaskType.URGENT,
                                onClick = { editedTaskType = TaskType.URGENT },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White
                                )
                            )
                            Text("Urgent", color = Color.White)
                        }
                    }
                }
            },
            backgroundColor = Color(0xFF1F2E43),
            contentColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
