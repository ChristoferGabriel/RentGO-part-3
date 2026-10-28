package com.example.rentgo

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import com.example.rentgo.navigation.AppNavigation
import com.example.rentgo.ui.theme.RentGoCores

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = RentGoCores.Azul,
                    background = RentGoCores.Fundo,
                    surface = RentGoCores.Branco
                )
            ) {
                AppNavigation()
            }
        }
    }
}
