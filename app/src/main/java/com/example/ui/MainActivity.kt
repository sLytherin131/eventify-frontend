package com.example.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.ui.theme.EventifyTheme

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EventifyTheme {
                val loginSuccess by loginViewModel.loginSuccess.collectAsState()
                val jwtToken by loginViewModel.jwtToken.collectAsState()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = if (jwtToken != null) "home" else "login"
                ) {
                    // LOGIN SCREEN
                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onForgotPasswordClick = {
                                navController.navigate("forgot_password")
                            },
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // FORGOT PASSWORD SCREEN
                    composable("forgot_password") {
                        ForgotPasswordPage(navController = navController)
                    }

                    // HOME SCREEN
                    composable("home") {
                        when {
                            jwtToken != null -> {
                                HomePageScreen(navController = navController, jwtToken = jwtToken!!)
                            }
                            else -> {
                                // Tampilkan indikator loading agar tidak langsung navigasi ulang
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }

                    // EVENT LIST SCREEN
                    composable("list_event") {
                        ListEventScreen(navController = navController)
                    }

                    // CALENDAR SCREEN
                    composable("calendar") {
                        CalendarPage(navController = navController)
                    }

                    // PERSONAL ADMIN SCREEN
                    composable("personal_admin/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        PersonalAdminScreen(
                            navController = navController,
                            jwtToken = token,
                            onLogout = {
                                loginViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }

                    // ✅ CREATE ADMIN SCREEN
                    composable("create_admin/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        CreateAdminPage(navController, token)
                    }

                    // ADD MEMBER PAGE
                    composable("add_member/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        AddMemberPage(navController, jwtToken = token)
                    }

                    composable("create_event/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        CreateEventScreen(navController = navController, jwtToken = token)
                    }

                    composable("chart_page") {
                        ChartPage(navController = navController)
                    }

                }
            }
        }
    }
}
