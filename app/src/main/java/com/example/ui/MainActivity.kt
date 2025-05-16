package com.example.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.ui.ui.theme.EventifyTheme

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EventifyTheme {
                val loginSuccess by loginViewModel.loginSuccess.collectAsState()

                if (loginSuccess) {
                    HomePageScreen()
                } else {
                    LoginScreen(viewModel = loginViewModel)
                }
            }
        }
    }
}
