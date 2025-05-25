package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// --- Data & API ---

data class LoginRequest(
    val identifier: String,
    val password: String
)

interface ApiService {
    @POST("/admin/login")
    suspend fun login(@Body request: LoginRequest): Response<ResponseBody>
}

// --- ViewModel ---

class LoginViewModel : ViewModel() {
    var identifier by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var loginResult by mutableStateOf<String?>(null)

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> get() = _loginSuccess

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8082")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun login() {
        if (identifier.isBlank() || password.isBlank()) {
            loginResult = "Please enter email/WhatsApp number and password"
            _loginSuccess.value = false
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.login(LoginRequest(identifier, password))
                if (response.isSuccessful) {
                    loginResult = "Login successful"
                    _loginSuccess.value = true
                } else {
                    val errorMsg = response.errorBody()?.string()
                    loginResult = "Login failed: ${errorMsg ?: "Unknown error"}"
                    _loginSuccess.value = false
                }
            } catch (e: Exception) {
                loginResult = "Login failed: ${e.message}"
                _loginSuccess.value = false
            } finally {
                isLoading = false
            }
        }
    }
}


// --- UI ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Warna yang disesuaikan
    val backgroundColor = Color(0xFF96AFC9)
    val cardColor = Color(0xFF1F2E43)
    val textColor = Color(0xFFFDF6D8)
    val buttonColor = Color(0xFF5D7E99)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Eventify",
                    style = MaterialTheme.typography.headlineLarge.copy(color = textColor)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.headlineMedium.copy(color = textColor)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = viewModel.identifier,
                            onValueChange = { viewModel.identifier = it },
                            label = { Text("Email / WhatsApp Number", color = textColor) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = textColor,
                                unfocusedBorderColor = textColor,
                                focusedLabelColor = textColor,
                                unfocusedLabelColor = textColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = viewModel.password,
                            onValueChange = { viewModel.password = it },
                            label = { Text("Password", color = textColor) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = textColor
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = textColor,
                                unfocusedBorderColor = textColor,
                                focusedLabelColor = textColor,
                                unfocusedLabelColor = textColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Forgot password?",
                            style = MaterialTheme.typography.labelSmall.copy(color = textColor)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.login() },
                            enabled = !viewModel.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(
                                    color = textColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Login", color = textColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        viewModel.loginResult?.let {
                            val color = if (it == "Login successful") Color.Green else Color.Red
                            Text(it, color = color)
                        }
                    }
                }
            }
        }
    }
}
