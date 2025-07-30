package com.example.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R
import com.example.api.EventWithDetailsResponse
import com.example.api.createApiService
import com.example.utils.truncate
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.unit.IntOffset

@OptIn(ExperimentalPagerApi::class)
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

    val pagerState = rememberPagerState()

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

    val coroutineScope = rememberCoroutineScope()
    var upcomingEvents by remember { mutableStateOf<List<EventWithDetailsResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val api = createApiService(jwtToken)
        coroutineScope.launch {
            try {
                val allEvents = api.getEvents()
                val now = System.currentTimeMillis()
                upcomingEvents = allEvents.filter { it.event.endTime >= now }
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
            TopAppBar(backgroundColor = navBarColor, elevation = 4.dp) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                    .padding(end = 5.dp, bottom = 5.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = navBarColor)
                    .clickable { navController.navigate("calendar/$jwtToken") },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = lightCream)
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 75.dp)
            ) {
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val now = System.currentTimeMillis()

                val sortedEvents = upcomingEvents.sortedWith(
                    compareBy<EventWithDetailsResponse> {
                        when (now) {
                            in it.event.startTime..it.event.endTime -> 0
                            else -> 1
                        }
                    }.thenBy { it.event.startTime }
                )

                if (sortedEvents.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalPager(
                            count = sortedEvents.size,
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                        ) { page ->
                            val ev = sortedEvents[page]
                            val start = Date(ev.event.startTime)
                            val end = Date(ev.event.endTime)
                            val isPast = now > ev.event.endTime

                            Card(
                                backgroundColor = Color(0xFF243447),
                                shape = RoundedCornerShape(20.dp),
                                elevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 3.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = ev.event.name,
                                        color = lightCream,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale("id", "ID"))

                                    Text(
                                        text = "${dateFormat.format(start)} - ${timeFormat.format(start)}\n${dateFormat.format(end)} - ${timeFormat.format(end)}",
                                        color = lightCream,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = ev.event.description?.truncate(60) ?: "-",
                                        color = lightCream,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Justify,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
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
                                                navController.navigate("create_event/$jwtToken?editId=${ev.event.id}")
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isPast) Color(0xFF2ECC71) else Color.Gray),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isPast) {
                                                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalPagerIndicator(
                            pagerState = pagerState,
                            modifier = Modifier.padding(4.dp),
                            activeColor = Color.White,
                            inactiveColor = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 👇 Task-list Personal Card
                Card(
                    backgroundColor = cardColor,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .height(310.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "Task-list Personal",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            IconButton(
                                onClick = { showDialog = true },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp) // Ukuran tombol bisa diatur sesuai preferensi
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f)), // Abu-abu transparan
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.White
                                    )
                                }
                            }

                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (taskViewModel.tasks.isEmpty()) {
                            Text("No personal tasks yet.", color = Color.White)
                        } else {
                            val scrollState = rememberScrollState()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(scrollState)
                                        .padding(end = 8.dp)
                                ) {
                                    val getTypeColor: (String) -> Color = {
                                        when (it) {
                                            "Personal" -> Color(0xFFB5E48C)
                                            "Work" -> Color(0xFFFFF59D)
                                            "Urgent" -> Color(0xFFE57373)
                                            else -> lightCream
                                        }
                                    }

                                    taskViewModel.tasks.asReversed().forEach { task ->
                                        val typeColor = getTypeColor(task.taskType)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FiberManualRecord,
                                                contentDescription = null,
                                                tint = typeColor,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text(
                                                text = task.description,
                                                color = typeColor,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 8.dp)
                                            )
                                            IconButton(onClick = { taskViewModel.deleteTask(task.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                            }
                                        }
                                    }
                                }

                                VerticalScrollbar(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxHeight(),
                                    scrollState = scrollState,
                                    color = lightCream.copy(alpha = 0.6f)
                                )
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

// ✅ Tambahan: Scrollbar komponen
@Composable
fun VerticalScrollbar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    width: Dp = 4.dp,
    color: Color = Color.LightGray
) {
    val proportion = scrollState.maxValue.toFloat().takeIf { it > 0 }?.let {
        scrollState.value.toFloat() / it
    } ?: 0f

    val heightFraction = scrollState.maxValue.takeIf { it > 0 }?.let {
        220f / (it + 220f)
    } ?: 0.2f

    Box(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(heightFraction)
                .align(Alignment.TopStart)
                .offset { IntOffset(0, (proportion * 220f).toInt()) }
                .background(color = color, shape = RoundedCornerShape(2.dp))
        )
    }
}
