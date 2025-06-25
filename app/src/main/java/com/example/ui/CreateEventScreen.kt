package com.example.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import java.util.*
import com.example.app.R

@Composable
fun CreateEventScreen(navController: NavController) {
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

    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.Add, Icons.Default.PieChart, Icons.Default.Person)
    val items = listOf("Home", "List", "Create", "Settings", "Account")

    val dummyMembers = remember {
        mutableStateListOf(
            "Name - Whatsapp Number",
            "Name - Whatsapp Number"
        )
    }

    val dummyTasks = remember {
        mutableStateListOf(
            "when an unknown principle is followed.",
            "when an unknown principle is broken."
        )
    }

    val taskColors = listOf(Color(0xFFB89B1F), Color(0xFF9B2C40))

    var showTaskDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun pickDateTime(onPicked: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            TimePickerDialog(context, { _: TimePicker, hour: Int, minute: Int ->
                calendar.set(year, month, dayOfMonth, hour, minute)
                val date = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale.getDefault()).format(calendar.time)
                onPicked(date)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

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
                                .background(color = backgroundColor)
                                .clickable {
                                    selectedIndex = index
                                    when (index) {
                                        0 -> navController.navigate("home")  // ✅ tombol home ke HomePageScreen.kt
                                        1 -> navController.navigate("list_event")
                                        2 -> navController.navigate("create_event")
                                        3 -> navController.navigate("chart_page")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
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
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Create Event", color = lightCream, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = eventName,
                            onValueChange = { eventName = it },
                            label = { Text("Event Name:", color = lightCream) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description:", color = lightCream) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Time Start:", color = lightCream)
                        OutlinedTextField(
                            value = timeStart,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable { pickDateTime { selected -> timeStart = selected } },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.LightGray,
                                disabledTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Time End:", color = lightCream)
                        OutlinedTextField(
                            value = timeEnd,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable { pickDateTime { selected -> timeEnd = selected } },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.LightGray,
                                disabledTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Member:", color = lightCream, modifier = Modifier.weight(1f))
                            OutlinedTextField(
                                value = searchMember,
                                onValueChange = { searchMember = it },
                                placeholder = {
                                    Text(
                                        "Search...",
                                        color = Color.LightGray,
                                        fontSize = 12.sp // Ukuran font placeholder diperkecil
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = lightCream
                                    )
                                },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(48.dp), // Tetap 48.dp
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 13.sp, // Ukuran font teks input juga diperkecil
                                    color = Color.White
                                ),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            backgroundColor = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("Members", fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                dummyMembers.forEachIndexed { index, member ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("• $member", modifier = Modifier.weight(1f))
                                        IconButton(onClick = { dummyMembers.removeAt(index) }) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Add Task:",
                                color = lightCream,
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f),
                                contentAlignment = Alignment.CenterEnd // ini yang bikin tombol + rata kanan
                            ) {
                                IconButton(
                                    onClick = { showTaskDialog = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Task",
                                        tint = cardColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        dummyTasks.forEachIndexed { index, task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp)) // Rounded
                                    .background(taskColors[index % taskColors.size])
                                    .padding(horizontal = 12.dp, vertical = 6.dp) // Tinggi lebih pendek
                            ) {
                                Text(
                                    "${index + 1}. ${task.take(25)}...",
                                    modifier = Modifier.weight(1f),
                                    color = Color.White
                                )
                                IconButton(onClick = { /* Edit */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }
                                IconButton(onClick = { dummyTasks.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Button(
                                onClick = { /* create event */ },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF6A8695),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.width(130.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Create")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showTaskDialog) {
        // Tambahkan dialog form task jika diperlukan
    }
}
