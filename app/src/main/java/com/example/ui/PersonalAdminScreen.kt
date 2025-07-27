package com.example.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.app.R
import androidx.compose.ui.layout.ContentScale

/* === Data Class === */
data class AdminRequest(
    @SerializedName("whatsappNumber") val whatsappNumber: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/* === Retrofit API === */
interface AdminApi {
    @GET("admin/me")
    suspend fun getMyAdmin(): AdminRequest

    @PUT("admin/{whatsappNumber}")
    suspend fun updateAdmin(
        @Path("whatsappNumber") whatsappNumber: String,
        @Body admin: AdminRequest
    ): AdminRequest
}

/* === Token Interceptor === */
class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

/* === Retrofit Client === */
fun provideAdminApi(token: String): AdminApi {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(token))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://eventify-kerja-praktek-copy-production.up.railway.app/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(AdminApi::class.java)
}

/* === ViewModel === */
class AdminViewModel(private val token: String) : ViewModel() {
    private val api = provideAdminApi(token)

    private val _admin = MutableStateFlow<AdminRequest?>(null)
    val admin: StateFlow<AdminRequest?> = _admin

    var name by mutableStateOf("")
    var whatsapp by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun fetchAdminData() {
        viewModelScope.launch {
            try {
                val response = api.getMyAdmin()
                _admin.value = response
                name = response.name
                whatsapp = response.whatsappNumber
                email = response.email
                password = response.password
            } catch (_: Exception) {}
        }
    }

    fun updateAdminData(onResult: (String) -> Unit) {
        viewModelScope.launch {
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                onResult("Please fill all fields")
                return@launch
            }

            try {
                val updatedAdmin = AdminRequest(
                    whatsappNumber = whatsapp,
                    name = name,
                    email = email,
                    password = password
                )
                val response = api.updateAdmin(whatsapp, updatedAdmin)
                _admin.value = response
                name = response.name
                email = response.email
                password = response.password
                onResult("Update successful")
            } catch (e: Exception) {
                onResult("Update failed")
            }
        }
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

/* === ViewModel Factory === */
class AdminViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel(token) as T
    }
}

/* === Composable Screen === */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PersonalAdminScreen(
    navController: NavController,
    jwtToken: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AdminViewModel = viewModel(factory = AdminViewModelFactory(jwtToken))
    val adminState by viewModel.admin.collectAsState()

    var resultMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchAdminData()
    }

    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                backgroundColor = Color(0xFF243447),
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
            BottomNavigationBar(navController = navController, jwtToken = jwtToken)
        },
        containerColor = Color(0xFF92B0BC)
    ) { innerPadding ->

    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1F2E43), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Text("Name: ", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(adminState?.name ?: "", color = Color.White)
                }
                Row {
                    Text("Whatsapp Number: ", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(adminState?.whatsappNumber ?: "", color = Color.White)
                }
                Row {
                    Text("Email: ", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(adminState?.email ?: "", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Change Information",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = { Text("Name:", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email:", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = viewModel.whatsapp,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Whatsapp Number:", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Gray,
                        disabledTextColor = Color.LightGray,
                        disabledLabelColor = Color.LightGray
                    )
                )

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Password:", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.White
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateAdminData { msg -> resultMessage = msg }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(130.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A8695)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Change")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.logout(context)
                        onLogout()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(130.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B2C40)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Sign Out")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.navigate("create_admin/${jwtToken}") },
                        modifier = Modifier.width(140.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A8695)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Create Admin")
                    }

                    Button(
                        onClick = { navController.navigate("add_member/${jwtToken}") },
                        modifier = Modifier.width(140.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A8695)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Add Member")
                    }
                }
            }
        }

        if (resultMessage != null) {
            AlertDialog(
                onDismissRequest = { resultMessage = null },
                confirmButton = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Button(
                            onClick = { resultMessage = null },
                            modifier = Modifier.width(130.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7E99)),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text("Ok", color = Color.White)
                        }
                    }
                },
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (resultMessage!!.contains("success", true)) "Success" else "Failed",
                            color = if (resultMessage!!.contains("success", true)) Color.Green else Color.Red
                        )
                    }
                },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(resultMessage!!, color = Color.White, textAlign = TextAlign.Center)
                    }
                },
                containerColor = Color(0xFF1F2E43)
            )
        }
    }
}

/* === Bottom Navigation Bar === */
@Composable
fun BottomNavigationBar(navController: NavController, jwtToken: String, modifier: Modifier = Modifier) {
    val navBarColor = Color(0xFF243447)
    val lightCream = Color(0xFFEEEECF)
    var selectedIndex by remember { mutableStateOf(4) }

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.List,
        Icons.Default.Add,
        Icons.Default.PieChart,
        Icons.Default.Person
    )
    val destinations = listOf(
        "home/$jwtToken",
        "list_event/$jwtToken",
        "create_event/$jwtToken",
        "chart_page/$jwtToken",
        "personal_admin/$jwtToken"
    )

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
            icons.forEachIndexed { index, icon ->
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
                            navController.navigate(destinations[index])
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint
                    )
                }
            }
        }
    }
}
