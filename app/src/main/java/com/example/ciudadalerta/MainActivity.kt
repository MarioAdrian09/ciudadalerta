package com.example.ciudadalerta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ciudadalerta.ui.theme.CiudadalertaTheme
import com.example.ciudadalerta.presentation.navigation.AppNavigation
import com.example.ciudadalerta.presentation.permissions.PermissionGate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // la Activity no crea nada, solo recibe el contenedor de la Application
        val container = (application as ciudadAlertaApp).container

        setContent {
            CiudadalertaTheme {
                PermissionGate {
                    AppNavigation(container = container)
                }
            }
        }
    }
}


