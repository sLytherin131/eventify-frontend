package com.example.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.TimePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch
import com.example.api.createApiService
import com.example.api.CreateEventRequest
import com.example.api.EventTask
import com.example.api.EventMember
import com.example.ui.MemberResponse

enum class TaskType {
    NORMAL, URGENT
}

@Composable
fun CreateEventScreen(
    navController: NavController,
    jwtToken: String,
    editId: String? = null
) {
    val backgroundColor = Color(0xFF92B0BC)
    val cardColor = Color(0xFF1F2E43)
    val lightCream = Color(0xFFEEEECF)
    val navBarColor = Color(0xFF243447)

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.List,
        Icons.Default.Add,
        Icons.Default.PieChart,
        Icons.Default.Person
    )
    val items = listOf("Home", "List", "Create", "Chart", "Profile")

    val api = remember { createApiService(jwtToken) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale("id", "ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }
    val nowStr = formatter.format(Date())

    var members by remember { mutableStateOf<List<MemberResponse>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            members = api.getMembers()
        } catch (e: Exception) {
            Log.e("CreateEvent", "Fetch members error: ${e.message}")
        }
    }

    var eventName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var timeStart by remember { mutableStateOf(nowStr) }
    var timeEnd by remember { mutableStateOf(nowStr) }

    var selectedIndex by remember { mutableStateOf(2) }
    var showEditTaskDialog by remember { mutableStateOf(false) }
    var editedTaskIndex by remember { mutableStateOf(-1) }
    var editedTaskMemo by remember { mutableStateOf("") }
    var editedTaskType by remember { mutableStateOf(TaskType.NORMAL) }

    val filteredMembers = members.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val eventMembers = remember { mutableStateListOf<MemberResponse>() }
    val dummyTasks = remember { mutableStateListOf<Pair<String, TaskType>>() }

    var showTaskDialog by remember { mutableStateOf(false) }
    var newTaskMemo by remember { mutableStateOf("") }
    var newTaskType by remember { mutableStateOf(TaskType.NORMAL) }

    LaunchedEffect(editId) {
        if (!editId.isNullOrEmpty()) {
            try {
                val response = api.getEventById(editId)
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        eventName = it.event.name
                        description = it.event.description ?: ""
                        timeStart = formatter.format(Date(it.event.startTime))
                        timeEnd = formatter.format(Date(it.event.endTime))

                        eventMembers.clear()
                        eventMembers.addAll(it.members.mapNotNull { m ->
                            members.find { mem -> mem.whatsappNumber == m.memberWhatsapp }
                        })

                        dummyTasks.clear()
                        dummyTasks.addAll(it.tasks.map { t ->
                            Pair(t.description, if (t.taskType == "urgent") TaskType.URGENT else TaskType.NORMAL)
                        })
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateEvent", "Load edit error: ${e.message}")
            }
        }
    }

    fun pickDateTime(onPicked: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            TimePickerDialog(context, { _: TimePicker, hour: Int, minute: Int ->
                calendar.set(year, month, dayOfMonth, hour, minute)
                onPicked(formatter.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun parseDateTimeToMillis(dateStr: String): Long {
        return formatter.parse(dateStr)?.time ?: System.currentTimeMillis()
    }

    fun postEvent() {
        coroutineScope.launch {
            try {
                val eventTasks = dummyTasks.map {
                    EventTask(it.first, it.second.name.lowercase(), System.currentTimeMillis())
                }
                val eventMembersBody = eventMembers.map {
                    EventMember(it.whatsappNumber)
                }
                val requestBody = CreateEventRequest(
                    eventName, description,
                    parseDateTimeToMillis(timeStart), parseDateTimeToMillis(timeEnd),
                    eventTasks, eventMembersBody
                )

                if (!editId.isNullOrEmpty()) {
                    val response = api.updateEvent(editId.toInt(), requestBody)

                    if (response.isSuccessful) {
                        Log.d("CreateEvent", "Update response: ${response.body()?.string()}")
                        navController.navigate("event_detail/$jwtToken/$editId")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CreateEvent", "Update failed: $errorBody")
                    }
                } else {
                    val response = api.createEvent(requestBody)
                    if (response.isSuccessful) {
                        val eventId = response.body()?.id ?: return@launch
                        navController.navigate("event_detail/$jwtToken/$eventId")
                    } else {
                        Log.e("CreateEvent", "Failed to create: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateEvent", "Error: ${e.message}")
            }
        }
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
                                        0 -> navController.navigate("home/$jwtToken")
                                        1 -> navController.navigate("list_event/$jwtToken")
                                        2 -> navController.navigate("create_event/$jwtToken")
                                        3 -> navController.navigate("chart_page/$jwtToken")
                                        4 -> navController.navigate("personal_admin/$jwtToken")
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Create Event", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = eventName,
                            onValueChange = { eventName = it },
                            label = { Text("Event Name:", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White,
                                cursorColor = Color.White,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description:", color = Color.White) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White,
                                cursorColor = Color.White,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Time Start:", color = Color.White)
                        OutlinedTextField(
                            value = timeStart,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { pickDateTime { timeStart = it } },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color.White,
                                disabledLabelColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Time End:", color = Color.White)
                        OutlinedTextField(
                            value = timeEnd,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { pickDateTime { timeEnd = it } },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color.White,
                                disabledLabelColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Add Member:",
                                color = Color.White,
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Search member...", fontSize = 12.sp, color = Color.LightGray)
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White,
                                    cursorColor = Color.White,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White
                                )
                            )
                        }

// ✅ Pindahkan LazyColumn keluar Row di bawah ini
                        if (searchQuery.isNotBlank()) {
                            if (filteredMembers.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 150.dp)
                                        .background(Color(0xFF2C3E50), shape = RoundedCornerShape(8.dp))
                                ) {
                                    items(filteredMembers) { member ->
                                        Text(
                                            text = "${member.name} (${member.whatsappNumber})",
                                            color = Color.White,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    if (!eventMembers.contains(member)) {
                                                        eventMembers.add(member)
                                                    }
                                                    searchQuery = "" // Clear query agar dropdown hilang
                                                }
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No results found",
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

// ✅ Lanjut komponen Card daftar member
                        Card(backgroundColor = Color.White, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Members", color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                                Spacer(modifier = Modifier.height(4.dp))
                                eventMembers.forEachIndexed { index, member ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                        Text("• ${member.name} (${member.whatsappNumber})", modifier = Modifier.weight(1f))
                                        IconButton(onClick = { eventMembers.removeAt(index) }) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Add Task:",
                                color = Color.White,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            IconButton(
                                onClick = { showTaskDialog = true },
                                modifier = Modifier
                                    .padding(start = 240.dp) // ⬅️ Padding kiri khusus tombol +
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = cardColor)
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { postEvent() },
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 20.dp), // ✅ Padding atas & bawah tombol
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    Text(
                        "Task",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp) // ⬅️ Atur padding atas & bawah
                    )
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            backgroundColor = Color.White,
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Type:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 20.dp), // ✅ Padding atas & bawah tombol
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    Text(
                        "Task",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
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

                    Text("Type:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
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
