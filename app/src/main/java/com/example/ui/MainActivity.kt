package com.example.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                val jwtToken by loginViewModel.jwtToken.collectAsState()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    // SPLASH SCREEN untuk memastikan jwtToken sudah siap
                    composable("splash") {
                        LaunchedEffect(jwtToken) {
                            if (jwtToken != null) {
                                navController.navigate("home/${jwtToken}") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            } else {
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // LOGIN SCREEN
                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onForgotPasswordClick = {
                                navController.navigate("forgot_password")
                            },
                            onLoginSuccess = {
                                navController.navigate("home/${loginViewModel.jwtToken.value}") {
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
                    composable("home/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        HomePageScreen(navController = navController, jwtToken = token)
                    }

                    // EVENT LIST SCREEN
                    composable("list_event/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        ListEventScreen(navController = navController, jwtToken = token)
                    }

                    // CALENDAR SCREEN
                    composable("calendar/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        CalendarPage(navController = navController, jwtToken = token)
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
                                    popUpTo("home/{token}") { inclusive = true }
                                }
                            }
                        )
                    }

                    // CREATE ADMIN SCREEN
                    composable("create_admin/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        CreateAdminPage(navController, token)
                    }

                    // ADD MEMBER SCREEN
                    composable("add_member/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        AddMemberPage(navController, jwtToken = token)
                    }

                    // CREATE EVENT SCREEN
                    composable("create_event/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        CreateEventScreen(navController = navController, jwtToken = token)
                    }

                    // CHART PAGE
                    composable("chart_page/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        ChartPage(navController = navController, jwtToken = token)
                    }

                    // EVENT DETAIL SCREEN
                    composable("event_detail/{token}/{eventId}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                        EventDetailsPage(navController, token, eventId)
                    }
                }
            }
        }
    }
}
