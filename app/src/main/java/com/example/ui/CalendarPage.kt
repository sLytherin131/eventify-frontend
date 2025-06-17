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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.*
import java.time.format.DateTimeFormatter
import androidx.navigation.NavHostController
import com.example.app.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun CalendarPage(navController: NavHostController) {
    val navBarColor = Color(0xFF243447)
    val lightBlue = Color(0xFF92B0BC)
    val lightCream = Color(0xFFEEEECF)
    val cardColor = Color(0xFF3D4148)

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedIndex by remember { mutableStateOf(3) } // Index 3 = Chart (Calendar)

    val daysInMonth = remember(currentMonth) {
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDay = currentMonth.lengthOfMonth()
        val days = mutableListOf<LocalDate?>()
        val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7
        repeat(dayOfWeekOffset) { days.add(null) }
        repeat(lastDay) { days.add(currentMonth.atDay(it + 1)) }
        days
    }

    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")

    val items = listOf("Home", "List", "Add", "Chart", "Personal")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.List,
        Icons.Default.Add,
        Icons.Default.PieChart,
        Icons.Default.Person
    )

    Scaffold(
        backgroundColor = lightBlue,
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
                                        0 -> navController.navigate("home")
                                        1 -> navController.navigate("list_event")
                                        2 -> navController.navigate("create_event")
                                        3 -> navController.navigate("calendar")
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
                Text(
                    text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() } + " ${currentMonth.year}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

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
                                        color = when {
                                            isSelected -> Color.Black
                                            else -> Color.White
                                        },
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = navBarColor)
                }

                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = navBarColor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Event", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                EventItem("Lorem Ipsum is simply dummy text", "8 April, 14.00 PM", cardColor)
                EventItem("Lorem Ipsum is simply dummy text", "23 April, 09.30 AM", cardColor)
            }
        }
    }
}

@Composable
fun EventItem(s: String, s1: String, cardColor: Color) {

}

