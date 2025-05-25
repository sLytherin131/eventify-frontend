package com.example.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.*
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
                    startDestination = if (loginSuccess) "home" else "login"
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
                        if (jwtToken != null) {
                            HomePageScreen(
                                navController = navController,
                                jwtToken = jwtToken!!
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
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
                    composable("personal_admin") {
                        PersonalAdminScreen(
                            navController = navController,
                            onLogout = {
                                loginViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }

                    // ✅ CREATE ADMIN SCREEN
                    composable("create_admin") {
                        CreateAdminPage(navController = navController)
                    }

                    // ADD MEMBER PAGE
                    composable("add_member") {
                        AddMemberPage(navController = navController)
                    }

                }
            }
        }
    }
}
