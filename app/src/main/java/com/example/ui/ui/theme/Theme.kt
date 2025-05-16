package com.example.ui.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Warna dari desain gambar
private val CustomLightColorScheme = lightColorScheme(
    primary = Color(0xFFDDE5CC),       // Untuk teks "Eventify" dan button text
    onPrimary = Color(0xFF223344),     // Warna teks di atas primary
    background = Color(0x96A7C5D1),    // Latar belakang utama (biru abu soft)
    surface = Color(0xFF1F2D3D),       // Warna card (gelap kebiruan)
    onSurface = Color(0xFFDDE5CC),     // Warna teks di atas card
    secondary = Color(0xFF607B94),     // Warna button login
    onSecondary = Color(0xFFDDE5CC),   // Warna teks button login
)

private val CustomDarkColorScheme = darkColorScheme(
    primary = Color(0xFFDDE5CC),
    background = Color(0xFF0D1B2A),
    surface = Color(0xFF1B263B),
    onPrimary = Color.White,
    onSurface = Color(0xFFE0E1DD),
    secondary = Color(0xFF415A77),
    onSecondary = Color.White,
)

@Composable
fun EventifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CustomDarkColorScheme
        else -> CustomLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // pastikan file Typography sudah kamu definisikan
        content = content
    )
}
