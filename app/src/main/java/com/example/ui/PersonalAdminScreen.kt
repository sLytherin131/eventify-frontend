package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem

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
    ): Void
}

/* === Token Interceptor === */
class AuthInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getTokenFromPrefs(context)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }

    private fun getTokenFromPrefs(context: Context): String {
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        return prefs.getString("token", "") ?: ""
    }
}

/* === Retrofit Client === */
fun provideAdminApi(context: Context): AdminApi {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(context))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8082")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(AdminApi::class.java)
}

/* === ViewModel === */
class AdminViewModel(application: Application, context: Context) : AndroidViewModel(application) {
    private val api = provideAdminApi(context)

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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAdminData() {
        viewModelScope.launch {
            try {
                val updatedAdmin = AdminRequest(
                    whatsappNumber = whatsapp,
                    name = name,
                    email = email,
                    password = password
                )
                api.updateAdmin(whatsapp, updatedAdmin)
                fetchAdminData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

/* === Composable Screen === */
@Composable
fun PersonalAdminScreen(
    navController: NavController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember {
        AdminViewModel(context.applicationContext as Application, context)
    }
    val adminState by viewModel.admin.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAdminData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFBFD6DB))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 64.dp)
                .fillMaxWidth(0.9f)
                .background(Color(0xFF213B54), shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Name: ${adminState?.name ?: ""}", color = Color.White)
            Text("Whatsapp Number: ${adminState?.whatsappNumber ?: ""}", color = Color.White)
            Text("Email: ${adminState?.email ?: ""}", color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Change Information", color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            val fields = listOf<Triple<String, String, (String) -> Unit>>(
                Triple(viewModel.name, "Name:", { viewModel.name = it }),
                Triple(viewModel.email, "Email:", { viewModel.email = it }),
                Triple(viewModel.whatsapp, "Whatsapp Number:", { viewModel.whatsapp = it }),
                Triple(viewModel.password, "Password:", { viewModel.password = it })
            )

            fields.forEach { (value, label, onChange) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = { Text(label, color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = label != "Whatsapp Number:",
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.LightGray,
                        cursorColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val buttonColor = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A8695),
                contentColor = Color.White
            )

            Button(
                onClick = { viewModel.updateAdminData() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = buttonColor,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B2C40),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Sign Out")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("create_admin") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = buttonColor,
                shape = RoundedCornerShape(50)
            ) {
                Text("Create Admin")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("add_member")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = buttonColor,
                shape = RoundedCornerShape(50)
            ) {
                Text("Add Member")
            }
        }

        BottomNavigationBar(navController, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/* === Bottom Navigation Bar === */
@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val navBarColor = Color(0xFF243447)
    val lightCream = Color(0xFFEEEECF)
    var selectedIndex by remember { mutableStateOf(4) } // default ke halaman personal

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.List,
        Icons.Default.Add,
        Icons.Default.PieChart,
        Icons.Default.Person
    )
    val destinations = listOf(
        "home",
        "list_event",
        "add",
        "chart",
        "personal_admin"
    )

    BottomNavigation(
        modifier = modifier,
        backgroundColor = navBarColor,
        contentColor = lightCream
    ) {
        icons.forEachIndexed { index, icon ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedIndex == index) Color.LightGray else lightCream
                    )
                },
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    navController.navigate(destinations[index])
                },
                alwaysShowLabel = false
            )
        }
    }
}
