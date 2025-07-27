package com.example.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TaskViewModel
import com.example.ui.TaskViewModelFactory
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun ChartPage(navController: NavController, jwtToken: String, taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(jwtToken))) {
    val backgroundColor = Color(0xFF92B0BC)
    val cardColor = Color(0xFF1F2E43)
    val lightCream = Color(0xFFEEEECF)
    val navBarColor = Color(0xFF243447)

    // sudah digantikan oleh `pieData` dari atas

    val pieColors = listOf(Color(0xFFB5E48C), Color(0xFFFFF59D), Color(0xFFE57373))
    val barColor = Color(0xFF8BF4FF)

    var selectedIndex by remember { mutableStateOf(3) }

    val items = listOf("Home", "List", "Add", "Chart", "Personal")
    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.Add, Icons.Default.PieChart, Icons.Default.Person)

    val now = Calendar.getInstance()
    val currentMonth = now.get(Calendar.MONTH)
    val currentYear = now.get(Calendar.YEAR)

    val tasksThisMonth = taskViewModel.tasks.filter {
        val taskDate = it.createdAt?.let { ts ->
            // createdAt harus bertipe Long atau ISO string
            Date(ts)
        } ?: return@filter false
        val cal = Calendar.getInstance().apply { time = taskDate }
        cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
    }

    val eventViewModel: EventViewModel = viewModel(factory = EventViewModelFactory(jwtToken))
// Hitung jumlah per tipe
    val taskTypeCounts = tasksThisMonth.groupingBy { it.taskType }.eachCount()

    val pieData = listOf(
        "Personal" to (taskTypeCounts["Personal"] ?: 0).toFloat(),
        "Work" to (taskTypeCounts["Work"] ?: 0).toFloat(),
        "Urgent" to (taskTypeCounts["Urgent"] ?: 0).toFloat()
    )

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val weekStart = calendar.time

    calendar.add(Calendar.DAY_OF_WEEK, 6)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val weekEnd = calendar.time

    val barDataMap = mutableMapOf(
        "Mon" to 0, "Tue" to 0, "Wed" to 0,
        "Thu" to 0, "Fri" to 0, "Sat" to 0, "Sun" to 0,
    )

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    for (eventWithDetails in eventViewModel.events) {
        val eventDate = try {
            Date(eventWithDetails.event.startTime)  // ← ambil startTime dari event
        } catch (e: Exception) {
            null
        } ?: continue

        if (eventDate >= weekStart && eventDate <= weekEnd) {
            val cal = Calendar.getInstance().apply { time = eventDate }
            val dayName = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                Calendar.SUNDAY -> "Sun"
                else -> ""
            }
            val memberCount = eventWithDetails.members.size  // ← ambil dari field `members`
            barDataMap[dayName] = barDataMap[dayName]?.plus(memberCount) ?: memberCount
        }
    }

    val barData = listOf(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    ).map { it to (barDataMap[it] ?: 0) }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            TopAppBar(backgroundColor = navBarColor) {
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
                    items.forEachIndexed { index, _ ->
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
                            Icon(imageVector = icons[index], contentDescription = items[index], tint = iconTint)
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
            // Pie Chart
            Card(
                backgroundColor = cardColor,
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Monthly Task Personal", color = lightCream, fontSize = 16.sp)
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 16.dp)) {
                        val total = pieData.sumOf { it.second.toDouble() }.toFloat()
                        var startAngle = -90f
                        val radius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        pieData.forEachIndexed { index, (_, value) ->
                            val sweepAngle = value / total * 360f
                            drawArc(
                                color = pieColors[index % pieColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        pieData.forEachIndexed { index, (label, value) ->
                            val color = pieColors[index % pieColors.size]
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(color, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = value.toInt().toString(),
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    color = lightCream,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart
            Card(
                backgroundColor = cardColor,
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 👇 Tambahkan di sini
                    Text(
                        text = "Periode: ${dateFormat.format(weekStart)} - ${dateFormat.format(weekEnd)}",
                        color = lightCream,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sudah ada sebelumnya
                    Text("Weekly Event Participant", color = lightCream, fontSize = 16.sp)
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(start = 15.dp, bottom = 25.dp, top = 16.dp)) {
                        val barWidth = size.width / (barData.size * 2)
                        val maxVal = 20f

                        // Draw Y Axis
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 2f
                        )

                        // Tambahkan label angka Y (0 - 20 dengan interval 5)
                        val step = 5
                        val labelPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }

                        for (i in 0..20 step step) {
                            val yPos = size.height - (i / 20f) * size.height
                            drawContext.canvas.nativeCanvas.drawText(
                                i.toString(),
                                -8f, // posisi X, negatif agar di kiri sumbu Y
                                yPos + 10f, // posisi Y
                                labelPaint
                            )
                        }

                        // Draw X Axis
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 2f
                        )

                        // Draw Bars
                        barData.forEachIndexed { index, (label, value) ->
                            val x = index * barWidth * 2 + barWidth / 2
                            val barHeight = (value / maxVal) * size.height
                            val y = size.height - barHeight

                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )

                            // Draw Labels
                            drawContext.canvas.nativeCanvas.drawText(
                                label,
                                x + barWidth / 2 - 24,
                                size.height + 30,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = 32f
                                    textAlign = android.graphics.Paint.Align.LEFT
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
