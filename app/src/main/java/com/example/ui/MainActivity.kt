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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                    // SPLASH SCREEN
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

                    // LOGIN
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

                    // FORGOT PASSWORD
                    composable("forgot_password") {
                        ForgotPasswordPage(navController = navController)
                    }

                    // HOME
                    composable("home/{jwtToken}",
                        arguments = listOf(navArgument("jwtToken") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        HomePageScreen(navController = navController, jwtToken = jwtToken)
                    }

                    // EVENT LIST
                    composable("list_event/{jwtToken}",
                        arguments = listOf(navArgument("jwtToken") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        ListEventScreen(navController = navController, jwtToken = jwtToken)
                    }

                    // CALENDAR
                    composable("calendar/{jwtToken}",
                        arguments = listOf(navArgument("jwtToken") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        CalendarPage(navController = navController, jwtToken = jwtToken)
                    }

                    // PERSONAL ADMIN
                    composable("personal_admin/{jwtToken}",
                        arguments = listOf(navArgument("jwtToken") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        PersonalAdminScreen(
                            navController = navController,
                            jwtToken = jwtToken,
                            onLogout = {
                                loginViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("home/{jwtToken}") { inclusive = true }
                                }
                            }
                        )
                    }

                    // CREATE ADMIN
                    composable("create_admin/{jwtToken}",
                        arguments = listOf(navArgument("jwtToken") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        CreateAdminPage(navController, jwtToken)
                    }

                    // ADD MEMBER
                    composable("add_member/{jwtToken}",
                        arguments = listOf(navArgument("jwtToken") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        AddMemberPage(navController, jwtToken)
                    }

                    // CREATE / EDIT EVENT
                    composable(
                        route = "create_event/{jwtToken}?editId={editId}",
                        arguments = listOf(
                            navArgument("jwtToken") { type = NavType.StringType },
                            navArgument("editId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        val editId = backStackEntry.arguments?.getString("editId")
                        CreateEventScreen(navController = navController, jwtToken = jwtToken, editId = editId)
                    }

                    // CHART PAGE
                    composable("chart_page/{jwtToken}",
                        arguments = listOf(navArgument("jwtToken") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        ChartPage(navController = navController, jwtToken = jwtToken)
                    }

                    // EVENT DETAIL
                    composable("event_detail/{jwtToken}/{eventId}",
                        arguments = listOf(
                            navArgument("jwtToken") { type = NavType.StringType },
                            navArgument("eventId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val jwtToken = backStackEntry.arguments?.getString("jwtToken") ?: ""
                        val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                        EventDetailsPage(navController = navController, jwtToken, eventId)
                    }
                }
            }
        }
    }
}
