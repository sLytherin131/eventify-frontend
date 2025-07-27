package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.api.createApiService
import com.example.api.EventResponse
import com.example.app.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun EventDetailsPage(
    navController: NavController,
    jwtToken: String,
    eventId: String
) {
    val context = LocalContext.current
    val api = remember { createApiService(jwtToken) }
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = Color(0xFF92B0BC)
    val cardColor = Color(0xFF1F2E43)
    val navBarColor = Color(0xFF243447)
    val mustard = Color(0xFFB89B1F)
    val red = Color(0xFF9B2C40)

    var event by remember { mutableStateOf<EventResponse?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    var isExpired by remember { mutableStateOf(false) }
    var loadFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        var success = false
        repeat(3) {
            try {
                val response = api.getEventById(eventId)
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        // Gabungkan task dan member langsung ke objek EventResponse
                        val enrichedEvent = it.event.copy(
                            eventTasks = it.tasks,
                            eventMembers = it.members
                        )
                        event = enrichedEvent
                        isExpired = enrichedEvent.endTime < System.currentTimeMillis()
                    }
                    return@LaunchedEffect
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(500)
        }
        if (!success) {
            loadFailed = true
        }
    }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            TopAppBar(
                backgroundColor = navBarColor,
                elevation = 4.dp,
                modifier = Modifier.height(56.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
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
        }
    ) { padding ->
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text("Delete Event", fontWeight = FontWeight.Bold, color = Color.Red)
                },
                text = {
                    Text("Are you sure you want to delete this event?", color = Color.White)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isDeleting = true
                                try {
                                    val deleteResponse = api.deleteEvent(eventId.toInt())
                                    if (deleteResponse.isSuccessful) {
                                        navController.navigate("list_event/$jwtToken") {
                                            popUpTo("event_details/$eventId") { inclusive = true }
                                        }
                                    } else {
                                        // Optional: Show Snackbar atau Dialog error
                                    }
                                } catch (e: Exception) {
                                    // Optional: Show Snackbar atau Dialog error
                                } finally {
                                    isDeleting = false
                                    showDeleteDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text("Yes", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteDialog = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text("No", color = Color.White)
                    }
                },
                backgroundColor = Color(0xFF1F2E43),
                contentColor = Color.White
            )
        }

        if (loadFailed) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Failed to load event. Please try again.", color = Color.White)
            }
        } else {
            event?.let { ev ->
                val formatter = SimpleDateFormat("EEEE - HH:mm, d MMMM yyyy", Locale.getDefault())
                val startFormatted = formatter.format(Date(ev.startTime))
                val endFormatted = formatter.format(Date(ev.endTime))

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        backgroundColor = cardColor,
                        shape = RoundedCornerShape(16.dp),
                        elevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                ev.name.uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Time Start:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(startFormatted, color = Color.White, textAlign = TextAlign.End)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Time End:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(endFormatted, color = Color.White, textAlign = TextAlign.End)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Description:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                ev.description ?: "-",
                                color = Color.White,
                                textAlign = TextAlign.Justify
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Task-list:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))

                            val taskList = ev.eventTasks.orEmpty()

                            if (taskList.isEmpty()) {
                                Text("No tasks.", color = Color.White)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    taskList.forEachIndexed { index, task ->
                                        val taskColor = if (task.taskType.equals("urgent", ignoreCase = true)) red else mustard
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(taskColor, RoundedCornerShape(6.dp))
                                                .padding(8.dp)
                                        ) {
                                            Text("${index + 1}. ${task.description}", color = Color.White)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    navController.navigate("create_event/$jwtToken?editId=$eventId")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isExpired) Color(0xFF2ECC71) else Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isExpired) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Done",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { navController.navigate("list_event/$jwtToken") },
                            colors = ButtonDefaults.buttonColors(backgroundColor = cardColor, contentColor = Color.White),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text("Back")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF9B2C40), contentColor = Color.White),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.width(130.dp),
                            enabled = !isDeleting
                        ) {
                            if (isDeleting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Delete")
                            }
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}