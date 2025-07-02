package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.R
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response
import com.example.ui.MemberResponse
import com.example.ui.MemberRequest

interface MemberApi {
    @GET("/members")
    suspend fun getMembers(): List<MemberResponse>

    @PUT("/members/{whatsappNumber}")
    suspend fun updateMember(
        @Path("whatsappNumber") whatsappNumber: String,
        @Body body: MemberRequest
    ): Response<Unit>

    @POST("/members")
    suspend fun createMember(@Body body: MemberRequest): MemberResponse
}

fun createApiService(token: String): MemberApi {
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token").build()
            chain.proceed(req)
        }.build()

    return Retrofit.Builder()
        .baseUrl("https://eventify-kerja-praktek-production.up.railway.app/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MemberApi::class.java)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberPage(navController: NavController, jwtToken: String) {
    val api = remember { createApiService(jwtToken) }
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var members by remember { mutableStateOf<List<MemberResponse>>(emptyList()) }
    var selectedMember by remember { mutableStateOf<MemberResponse?>(null) }

    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    var resultMessage by remember { mutableStateOf<String?>(null) }

    val whiteTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White,
        cursorColor = Color.White
    )

    LaunchedEffect(Unit) {
        try {
            members = api.getMembers()
        } catch (_: Exception) { }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF92B0BC))) {
        Column(modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp)) {
            Image(
                painter = painterResource(id = R.drawable.eventifylogo),
                contentDescription = "Eventify Logo",
                modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF1F2E43), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        selectedMember = null
                    },
                    placeholder = { Text("Search Name...", color = Color.White) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = whiteTextFieldColors
                )

                if (searchQuery.isNotBlank()) {
                    val filtered = members.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }

                    if (filtered.isNotEmpty()) {
                        LazyColumn(modifier = Modifier
                            .heightIn(max = 150.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(8.dp))) {
                            items(filtered) { member ->
                                Text(
                                    text = "${member.name} (${member.whatsappNumber})",
                                    color = Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedMember = member
                                            name = member.name
                                            number = member.whatsappNumber
                                            searchQuery = "" // optional: clear after selecting
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No results found",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Text(
                    text = "Name: ${selectedMember?.name ?: "-"}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Whatsapp Number: ${selectedMember?.whatsappNumber ?: "-"}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Member Information",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEEEECF),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name:", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = whiteTextFieldColors
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Whatsapp Number:", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = whiteTextFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                if (selectedMember != null) {
                                    api.updateMember(selectedMember!!.whatsappNumber, MemberRequest(number, name))
                                    resultMessage = "Update successful"
                                } else {
                                    api.createMember(MemberRequest(number, name))
                                    resultMessage = "Member created"
                                }
                                members = api.getMembers()
                                selectedMember = null
                                searchQuery = ""
                            } catch (e: Exception) {
                                resultMessage = "Error: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(130.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A8695),
                        contentColor = Color(0xFFEEEECF)
                    )
                ) {
                    Text("Submit", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterHorizontally).width(100.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF213B54),
                    contentColor = Color(0xFFEEEECF)
                )
            ) {
                Text("Back")
            }
        }

        resultMessage?.let {
            AlertDialog(
                onDismissRequest = { resultMessage = null },
                confirmButton = {
                    Button(onClick = { resultMessage = null }) {
                        Text("OK")
                    }
                },
                title = {
                    Text(if (it.contains("success", true) || it.contains("created", true)) "Success" else "Failed")
                },
                text = { Text(it) },
                containerColor = Color(0xFF1F2E43)
            )
        }
    }
}
