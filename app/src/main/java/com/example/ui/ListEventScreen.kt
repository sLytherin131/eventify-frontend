package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.api.EventWithDetailsResponse
import com.example.api.createApiService
import com.example.api.truncate
import com.example.app.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.utils.truncate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListEventScreen(navController: NavController, jwtToken: String) {
    val backgroundColor = Color(0xFF92B0BC)
    val navBarColor = Color(0xFF243447)
    val lightCream = Color(0xFFEEEECF)

    val items = listOf("Home", "List", "Add", "Chart", "Personal")
    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.Add, Icons.Default.PieChart, Icons.Default.Person)
    var selectedIndex by remember { mutableStateOf(1) }

    val api = remember { createApiService(jwtToken) }
    var events by remember { mutableStateOf<List<EventWithDetailsResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    fun sortEvents(list: List<EventWithDetailsResponse>): List<EventWithDetailsResponse> {
        val now = System.currentTimeMillis()
        return list.sortedWith(
            compareBy<EventWithDetailsResponse> {
                when {
                    now in it.event.startTime..it.event.endTime -> 0
                    it.event.startTime > now -> 1
                    else -> 2
                }
            }.thenBy {
                when {
                    now in it.event.startTime..it.event.endTime -> it.event.startTime
                    it.event.startTime > now -> it.event.startTime
                    else -> -it.event.endTime
                }
            }
        )
    }

    LaunchedEffect(currentMonth) {
        coroutineScope.launch {
            try {
                val allEvents = api.getEvents()
                val filtered = allEvents.filter {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = it.event.startTime
                    cal.get(Calendar.MONTH) + 1 == currentMonth
                }
                events = sortEvents(filtered)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            TopAppBar(
                backgroundColor = navBarColor,
                elevation = 4.dp
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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(navBarColor),
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
                        val bgColor = if (isSelected) Color.White else Color.Transparent
                        val iconTint = if (isSelected) navBarColor else lightCream

                        Box(
                            modifier = Modifier
                                .size(if (index == 2) 56.dp else 48.dp)
                                .clip(backgroundShape)
                                .background(bgColor)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = monthNames[currentMonth - 1],
                    onValueChange = {},
                    label = { Text("Filter by month") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    monthNames.forEachIndexed { index, month ->
                        DropdownMenuItem(onClick = {
                            currentMonth = index + 1
                            expanded = false
                        }) {
                            Text(month)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                if (events.isEmpty()) {
                    Text("No events this month.", color = Color.White)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(events) { ev ->
                            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val start = Date(ev.event.startTime)
                            val end = Date(ev.event.endTime)
                            val isPast = System.currentTimeMillis() > ev.event.endTime

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(170.dp),
                                backgroundColor = Color(0xFF243447),
                                shape = RoundedCornerShape(20.dp),
                                elevation = 8.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = ev.event.name,
                                        color = Color(0xFFEEEECF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "${dayFormat.format(start)} - ${timeFormat.format(start)}\n${dayFormat.format(end)} - ${timeFormat.format(end)}",
                                        color = Color(0xFFEEEECF),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = ev.event.description?.truncate(60) ?: "-",
                                        color = Color(0xFFEEEECF),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Justify,
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.weight(1f)) // Ini yang menjamin tombol tetap di bawah

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "More",
                                            color = Color.White,
                                            modifier = Modifier.clickable {
                                                navController.navigate("event_detail/$jwtToken/${ev.event.id}")
                                            }
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = {
                                                navController.navigate("create_event/${jwtToken}?editId=${ev.event.id}")
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = Color.White
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isPast) Color(0xFF2ECC71) else Color.Gray),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isPast) {
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
                            }
                        }
                    }
                }
            }
        }
    }
}
