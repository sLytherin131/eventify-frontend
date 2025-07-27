package com.example.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.app.R
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import io.ktor.client.call.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun CalendarPage(navController: NavHostController, jwtToken: String) {
    val navBarColor = Color(0xFF243447)
    val lightBlue = Color(0xFF92B0BC)
    val lightCream = Color(0xFFEEEECF)
    val cardColor = Color(0xFF1F2E43)

    var currentMonth by remember {
        mutableStateOf(YearMonth.now(ZoneId.of("Asia/Jakarta")))
    }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedIndex by remember { mutableStateOf(-1) }

    var eventList by remember { mutableStateOf(listOf<EventResponse>()) }

    val daysInMonth = remember(currentMonth) {
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDay = currentMonth.lengthOfMonth()
        val days = mutableListOf<LocalDate?>()
        val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7
        repeat(dayOfWeekOffset) { days.add(null) }
        repeat(lastDay) { days.add(currentMonth.atDay(it + 1)) }
        while (days.size % 7 != 0) { days.add(null) } // biar row fixed
        days
    }

    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH)

    val items = listOf("Home", "List", "Add", "Chart", "Personal")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.List,
        Icons.Default.Add,
        Icons.Default.PieChart,
        Icons.Default.Person
    )

    // Fetch events setiap kali bulan berubah
    LaunchedEffect(currentMonth) {
        try {
            val client = HttpClient(Android) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
            }

            val response = client.get("https://eventify-kerja-praktek-copy-production.up.railway.app/events") {
                header("Authorization", "Bearer $jwtToken")
            }.body<List<EventWrapper>>()

            val events = response.map { it.event }

            val filtered = events.filter {
                val date = Instant.ofEpochMilli(it.startTime).atZone(ZoneId.of("Asia/Jakarta")).toLocalDate()
                date.month == currentMonth.month && date.year == currentMonth.year
            }

            eventList = filtered.sortedByDescending { it.startTime }

        } catch (e: Exception) {
            println("Error fetch events: ${e.localizedMessage}")
        }
    }

    Scaffold(
        backgroundColor = lightBlue,
        topBar = {
            TopAppBar(backgroundColor = navBarColor, elevation = 4.dp) {
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
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatter.format(selectedDate),
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(navBarColor, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .background(navBarColor, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    Text(
                        text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() } + " ${currentMonth.year}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                        Text(text = it, color = Color.White, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val rows = daysInMonth.chunked(7)
                rows.forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        week.forEach { day ->
                            if (day == null) {
                                Box(modifier = Modifier.size(32.dp))
                            } else {
                                val isToday = day == LocalDate.now()
                                val isSelected = day == selectedDate

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            color = when {
                                                isSelected -> Color.White
                                                isToday -> Color.Yellow
                                                else -> Color.Transparent
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable { selectedDate = day },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.dayOfMonth.toString(),
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Event", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            if (eventList.isEmpty()) {
                Text("No events for this month", color = Color.White, fontSize = 14.sp)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Atur tinggi scrollable area sesuai kebutuhan
                ) {
                    LazyColumn {
                        items(eventList) { event ->
                            val startDate = Instant.ofEpochMilli(event.startTime).atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime()
                            val formatter = DateTimeFormatter.ofPattern("d MMMM, HH.mm", Locale.ENGLISH)
                            val formatted = formatter.format(startDate)

                            EventItem(event.name, formatted, cardColor)
                            Spacer(modifier = Modifier.height(8.dp)) // Tambah jarak antar event
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(name: String, dateTime: String, cardColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .padding(bottom = 8.dp)
    ) {
        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(dateTime, color = Color.White, fontSize = 12.sp)
    }
}

@Serializable
data class EventWrapper(
    val event: EventResponse
)

@Serializable
data class EventResponse(
    val id: Int,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val createdBy: String
)

