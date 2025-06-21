package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.R // Ganti sesuai nama package kamu

@Composable
fun ForgotPasswordPage(navController: NavController) {
    // Warna UI sesuai gambar
    val backgroundColor = Color(0xFF92B0BC)
    val cardColor = Color(0xFF1F2E43)
    val textColor = Color(0xFFFDF6D8)
    val buttonColor = Color(0xFF5D7E99)

    var emailOrPhone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Logo Eventify
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
                            value = emailOrPhone,
                            onValueChange = { emailOrPhone = it },
                            label = { Text("Email or Whatsapp Number:", color = textColor) },
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
                            onClick = { /* TODO: Send code action */ },
                            modifier = Modifier.width(130.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "Send Code",
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge // ✅ disamakan
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
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
                            value = newPassword,
                            onValueChange = { newPassword = it },
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
                            onClick = { /* TODO: Reset password action */ },
                            modifier = Modifier.width(130.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "Reset",
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge // ✅ disamakan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(130.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Login",
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge // ✅ disamakan
                    )
                }

            }
        }
    }
}
