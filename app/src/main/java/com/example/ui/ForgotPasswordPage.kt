package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.app.R
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- Data & API ---
data class ForgotPasswordRequest(val identifier: String)
data class ResetPasswordRequest(val identifier: String, val code: String, val newPassword: String)

interface ForgotPasswordApi {
    @POST("/admin/send-reset-code")
    suspend fun sendResetCode(@Body request: ForgotPasswordRequest): Response<ResponseBody>

    @POST("/admin/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResponseBody>
}

// --- ViewModel ---
class ForgotPasswordViewModel : ViewModel() {
    var identifier by mutableStateOf("")
    var code by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var message by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://eventify-kerja-praktek-copy-production.up.railway.app/") // Ganti sesuai IP backend kamu
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ForgotPasswordApi::class.java)

    fun sendCode() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = api.sendResetCode(ForgotPasswordRequest(identifier))
                message = if (response.isSuccessful) "Kode berhasil dikirim" else "Gagal kirim kode"
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
            isLoading = false
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = api.resetPassword(ResetPasswordRequest(identifier, code, newPassword))
                message = if (response.isSuccessful) "Password berhasil direset" else "Reset gagal"
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
            isLoading = false
        }
    }
}

// --- UI ---
@Composable
fun ForgotPasswordPage(navController: NavController) {
    val viewModel = remember { ForgotPasswordViewModel() }

    val backgroundColor = Color(0xFF92B0BC)
    val cardColor = Color(0xFF1F2E43)
    val textColor = Color(0xFFFDF6D8)
    val buttonColor = Color(0xFF5D7E99)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.message) {
        if (viewModel.message.isNotEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(viewModel.message)
                viewModel.message = ""
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = backgroundColor
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.eventifylogo),
                        contentDescription = "Eventify Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Reset Password",
                                fontSize = 20.sp,
                                color = textColor
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = viewModel.identifier,
                                onValueChange = { viewModel.identifier = it },
                                label = { Text("Email or WhatsApp Number:", color = textColor) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    focusedBorderColor = textColor,
                                    unfocusedBorderColor = textColor,
                                    focusedLabelColor = textColor,
                                    unfocusedLabelColor = textColor
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.sendCode() },
                                modifier = Modifier.width(130.dp),
                                enabled = !viewModel.isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Send Code", color = textColor, style = MaterialTheme.typography.bodyLarge)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = viewModel.code,
                                onValueChange = { viewModel.code = it },
                                label = { Text("Code:", color = textColor) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    focusedBorderColor = textColor,
                                    unfocusedBorderColor = textColor,
                                    focusedLabelColor = textColor,
                                    unfocusedLabelColor = textColor
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = viewModel.newPassword,
                                onValueChange = { viewModel.newPassword = it },
                                label = { Text("New Password:", color = textColor) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    focusedBorderColor = textColor,
                                    unfocusedBorderColor = textColor,
                                    focusedLabelColor = textColor,
                                    unfocusedLabelColor = textColor
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.resetPassword() },
                                modifier = Modifier.width(130.dp),
                                enabled = !viewModel.isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Reset", color = textColor, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .width(130.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Login", color = textColor, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
