package com.example.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import com.example.app.R
import org.json.JSONObject
import com.example.api.ApiService
import com.example.api.LoginRequest
import androidx.compose.ui.text.font.FontWeight // pastikan import ini ada

// --- Data & API ---

data class LoginRequest(
    val identifier: String,
    val password: String
)

// --- ViewModel ---

class LoginViewModel : ViewModel() {
    var identifier by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var loginResult by mutableStateOf<String?>(null)

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _jwtToken = MutableStateFlow<String?>(null)
    val jwtToken: StateFlow<String?> = _jwtToken.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://eventify-kerja-praktek-copy-production.up.railway.app/")
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
                val cleanIdentifier = identifier.trim().replace("\n", "").replace("\r", "")
                val response = apiService.login(LoginRequest(cleanIdentifier, password))
                if (response.isSuccessful) {
                    val bodyStr = response.body()?.string()
                    if (bodyStr != null) {
                        val jsonObj = JSONObject(bodyStr)
                        val token = jsonObj.optString("token", "")
                        if (token.isNotBlank()) {
                            _jwtToken.value = token
                            _loginSuccess.value = true
                        } else {
                            loginResult = "Login failed: token missing"
                            _loginSuccess.value = false
                        }
                    } else {
                        loginResult = "Login failed: Empty response"
                        _loginSuccess.value = false
                    }
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

    fun logout() {
        identifier = ""
        password = ""
        loginResult = null
        isLoading = false
        _loginSuccess.value = false
        _jwtToken.value = null
    }
    fun clearLoginResult() {
        loginResult = null
    }
}

// --- UI ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF92B0BC)
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val cardColor = Color(0xFF1F2E43)
    val textColor = Color(0xFFFDF6D8)
    val buttonColor = Color(0xFF5D7E99)

    // ✅ Reset loginResult saat screen pertama kali dibuka
    LaunchedEffect(Unit) {
        viewModel.clearLoginResult()
    }

    // --- Tangani hasil login ---
    LaunchedEffect(viewModel.loginResult) {
        viewModel.loginResult?.let {
            dialogMessage = it
            showDialog = true
        }
    }

    LaunchedEffect(viewModel.loginSuccess) {
        viewModel.loginSuccess.collectLatest { success ->
            if (success) {
                viewModel.jwtToken.value?.let { token ->
                    onLoginSuccess(token)
                }
            }
        }
    }

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
                Image(
                    painter = painterResource(id = R.drawable.eventifylogo),
                    contentDescription = "Eventify Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 8.dp)
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
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = textColor,
                                fontWeight = FontWeight.Bold // ← ini bikin bold
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = viewModel.identifier,
                            onValueChange = { viewModel.identifier = it },
                            label = { Text("Email / Nomor WhatsApp", color = textColor) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            shape = RoundedCornerShape(12.dp),
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
                                        contentDescription = if (passwordVisible) "Sembunyikan Password" else "Tampilkan Password",
                                        tint = textColor
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
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

                        Spacer(modifier = Modifier.height(20.dp)) // atas tombol
                        TextButton(onClick = onForgotPasswordClick) {
                            Text(
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.labelSmall.copy(color = textColor)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp)) // bawah tombol

                        Button(
                            onClick = { viewModel.login() },
                            modifier = Modifier.width(130.dp),
                            enabled = !viewModel.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(
                                    color = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text("Login", color = textColor, style = MaterialTheme.typography.bodyLarge)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // --- ✅ AlertDialog dengan reset loginResult ---
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        viewModel.clearLoginResult()
                    },
                    confirmButton = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Button(
                                onClick = {
                                    showDialog = false
                                    viewModel.clearLoginResult()
                                },
                                modifier = Modifier.width(130.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7E99)),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Text("Ok", color = textColor, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    },
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "Login Failed",
                                color = Color.Red,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    text = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                dialogMessage,
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                            containerColor = cardColor
                )
            }
        }
    }
}
