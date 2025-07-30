package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.api.CreateAdminRequest
import com.example.api.createPublicApiService
import com.example.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun CreateAdminPage(navController: NavController, jwtToken: String) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF92B0BC))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.eventifylogo),
                contentDescription = "Eventify Logo",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F2E43), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Admin",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 11.dp)
                )

                val inputModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)

                val whiteTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = inputModifier,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = whiteTextFieldColors
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Email or WhatsApp Number") },
                    modifier = inputModifier,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = whiteTextFieldColors
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = inputModifier,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = whiteTextFieldColors
                )

                Spacer(modifier = Modifier.height(11.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || contact.isBlank() || password.isBlank()) {
                            resultMessage = "All fields are required."
                            isSuccess = false
                            return@Button
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val api = createPublicApiService()
                                val request = CreateAdminRequest(
                                    whatsappNumber = contact,
                                    name = name,
                                    email = contact,
                                    password = password
                                )
                                val response = api.createAdmin(request)

                                if (response.isSuccessful) {
                                    resultMessage = "Admin '$name' with contact '$contact' has been successfully created."
                                    isSuccess = true
                                } else {
                                    resultMessage = when (response.code()) {
                                        409 -> "An admin with this contact ('$contact') already exists. Please use a different number or email."
                                        400 -> "Invalid data. Please check all fields and try again."
                                        else -> "Failed to create admin. Server responded with code: ${response.code()}"
                                    }
                                    isSuccess = false
                                }
                            } catch (e: Exception) {
                                resultMessage = when (e) {
                                    is HttpException -> {
                                        "Request failed with HTTP ${e.code()}: ${e.message()}"
                                    }
                                    else -> "Unexpected error: ${e.localizedMessage}"
                                }
                                isSuccess = false
                            }
                        }
                    },
                    modifier = Modifier
                        .width(130.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A8695),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Create")
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF213B54),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Back")
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
                            text = if (isSuccess) "Success" else "Failed",
                            color = if (isSuccess) Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }
                },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = resultMessage ?: "",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                containerColor = Color(0xFF1F2E43)
            )
        }
    }
}
